package ru.vtb.uasp.mdm.enrichment

import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsObject, JsValue, OWrites}
import ru.vtb.uasp.common.abstraction.FlinkStreamProducerPredef.{StreamExecutionEnvironmentPredef, StreamFactory}
import ru.vtb.uasp.common.base.EnrichFlinkDataStream.EnrichFlinkDataStreamSink
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.extension.CommonExtension.Also
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.kafka.FlinkSinkProperties.producerFactoryDefault
import ru.vtb.uasp.common.service.JsonConvertOutService.{IdentityPredef, JsonPredef}
import ru.vtb.uasp.common.service.UaspDeserializationProcessFunction
import ru.vtb.uasp.common.service.dto.{KafkaDto, PropertyWithSerializer, ServiceDataDto}
import ru.vtb.uasp.mdm.enrichment.service.JsValueConsumer
import ru.vtb.uasp.mdm.enrichment.service.dto.{KeyedCAData, KeyedUasp, NotStandardDataStreams, OutStreams}
import ru.vtb.uasp.mdm.enrichment.utils.config.MDMEnrichmentPropsModel.appPrefixDefaultName
import ru.vtb.uasp.mdm.enrichment.utils.config._


object EnrichmentJob extends Serializable {

  val keySelectorMain: KeySelector[KeyedUasp, String] =
    (in: KeyedUasp) => {
      in.localId
    }

  val keySelectorCa: KeySelector[KeyedCAData, String] =
    (in: KeyedCAData) => {
      in.key
    }

  private val logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    val propsModel = MDMEnrichmentPropsModel.configApp(appPrefixDefaultName, args)

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(propsModel.appSyncParallelism)
    //      env.enableCheckpointing(propsModel.initProperty.appStreamCheckpointTimeMilliseconds.value, CheckpointingMode.EXACTLY_ONCE)


    val flinkDataStreams = init(env, propsModel)

    val mainDataStream = process(
      flinkDataStreams = flinkDataStreams,
      propsModel)

    setSinks(mainDataStream, propsModel)

    env.execute(propsModel.serviceData.fullServiceName)

  }

  def init(env: StreamExecutionEnvironment, propsModel: MDMEnrichmentPropsModel, producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault): NotStandardDataStreams = {


    implicit val serviceData: ServiceDataDto = propsModel.serviceData

    val uaspDeserializationProcessFunction = UaspDeserializationProcessFunction()

    val serialisationProcessFunctionJsValue = new JsValueConsumer(serviceData)

    val mainDataStream = env
      .registerConsumerWithMetric(
        propsModel.serviceData,
        propsModel.allEnrichProperty.mainEnrichProperty.fromTopic,
        propsModel.allEnrichProperty.mainEnrichProperty.dlqTopicProp,
        uaspDeserializationProcessFunction,
        producerFabric)


    val commonStream = propsModel.allEnrichProperty.commonEnrichProperty
      .map { cns =>
        val commonEnrichPropertyDlq = propsModel.allEnrichProperty.commonEnrichProperty.flatMap(a => a.dlqTopicProp)
        env
          .registerConsumerWithMetric(
            propsModel.serviceData,
            cns.fromTopic,
            commonEnrichPropertyDlq, serialisationProcessFunctionJsValue, producerFabric)
      }


    val globalIdStream = propsModel.allEnrichProperty.globalIdEnrichProperty
      .map { cns =>
        val globalIdEnrichPropertyDlq = propsModel.allEnrichProperty.globalIdEnrichProperty.flatMap(a => a.dlqTopicProp)
        env.registerConsumerWithMetric(
          propsModel.serviceData,
          cns.fromTopic,
          globalIdEnrichPropertyDlq,
          serialisationProcessFunctionJsValue,
          producerFabric)
      }

    NotStandardDataStreams(mainDataStream, commonStream, globalIdStream)
  }

  /**
   * Заставили под пытками сделать подобный метод, даже боюсь словами описывать его логику.
   */
  def process(flinkDataStreams: NotStandardDataStreams,
              mDMEnrichmentPropsModel: MDMEnrichmentPropsModel,
              producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
             ): OutStreams = {

    implicit val oWritesJsValue: OWrites[JsValue] = (o: JsValue) => o.asInstanceOf[JsObject]
    implicit val fabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFabric


    val standartedDataStreams = mDMEnrichmentPropsModel.streamTransformService.transform(flinkDataStreams)

    val mainDlqProp = mDMEnrichmentPropsModel.allEnrichProperty.mainEnrichProperty.dlqTopicProp

    val streamGlobal = standartedDataStreams.mainDataStream
      // если настроено обогащение глобальнымнтификатором, то надо обогатить
      .also { mainDs =>
        val maybeGlobal = for {
          keyedMainStreamSrv <- mDMEnrichmentPropsModel.globalMainStreamExtractKeyFunction
          keyGlobalSrv <- mDMEnrichmentPropsModel.keyGlobalIdEnrichmentMapService
          globalIdStream <- standartedDataStreams.globalIdStream
        } yield (keyedMainStreamSrv, keyGlobalSrv, globalIdStream)
        maybeGlobal
          .map { tuple =>
            val (keyedMainStreamSrv, keyGlobalSrv, validatedGlobalIdStream) = tuple

            mainDs
              .processWithMaskedDqlF(
                mDMEnrichmentPropsModel.serviceData,
                keyedMainStreamSrv,
                mainDlqProp.map(p => PropertyWithSerializer(p, {_.serializeToKafkaJsValue})),
                fabric)
              .keyBy(keySelectorMain)
              .connect(validatedGlobalIdStream.keyBy(d => d.key))
              .process(keyGlobalSrv)
              .processWithMaskedDqlFC[UaspDto, UaspDto](
                mDMEnrichmentPropsModel.serviceData,
                mDMEnrichmentPropsModel.throwToDlqService,
                mainDlqProp.map(sp => sp -> { (q, w) => q.serializeToBytes(w) }),
                fabric
              )
          }.getOrElse(mainDs)
      }
    val streamCommon = streamGlobal
      // если настроено обогащение, то надо обогатаить. Тут меняются только мапки, поле id Не трогается
      .also { mainDs =>
        val maybeCommon = for {
          keyedMainStreamSrv <- mDMEnrichmentPropsModel.commonMainStreamExtractKeyFunction
          keyCommonEnrichmentMapService <- mDMEnrichmentPropsModel.keyCommonEnrichmentMapService
          commonStream <- standartedDataStreams.commonStream
        } yield (keyedMainStreamSrv, keyCommonEnrichmentMapService, commonStream)

        maybeCommon
          .map { tuple =>
            val (keyedMainStreamSrv, keyCommonEnrichmentMapService, validatedGlobalIdStream) = tuple

            mainDs
              .processWithMaskedDqlF(
                mDMEnrichmentPropsModel.serviceData,
                keyedMainStreamSrv,
                mainDlqProp.map(p => PropertyWithSerializer(p, {_.serializeToKafkaJsValue})),
                fabric)
              .keyBy(keySelectorMain)
              .connect(validatedGlobalIdStream.keyBy(d => d.key))
              .process(keyCommonEnrichmentMapService)
              .processWithMaskedDqlFC[UaspDto, UaspDto](
                mDMEnrichmentPropsModel.serviceData,
                mDMEnrichmentPropsModel.throwToDlqService,
                mainDlqProp.map(sp => sp -> { (q, w) => q.serializeToBytes(w) }),
                fabric
              )
          }.getOrElse(mainDs)
      }


    OutStreams(streamCommon)


  }

  def setSinks(outStreams: OutStreams,
               syncProperties: MDMEnrichmentPropsModel): Unit = {
    setMainSink(outStreams.mainStream, syncProperties)

  }

  def setMainSink(mainDataStream: DataStream[UaspDto],
                  syncProperties: MDMEnrichmentPropsModel,
                  producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                 ): DataStreamSink[KafkaDto] = {
    val mainProducer = syncProperties.flinkSinkPropertiesMainProducer.createSinkFunction(producerFabric)


    mainDataStream
      .map(q => q.serializeToBytes(None).right.get)
      .map(syncProperties.flinkSinkPropertiesMainProducer.prometheusMetric[KafkaDto](syncProperties.serviceData))
      .addSink(mainProducer)
      .enrichName(s"MAIN_SINK_outEnrichmentSink")
  }


}
