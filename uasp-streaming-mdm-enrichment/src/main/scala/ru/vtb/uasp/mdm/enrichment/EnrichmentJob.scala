package ru.vtb.uasp.mdm.enrichment

import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}
import org.slf4j.LoggerFactory
import ru.vtb.uasp.common.abstraction.FlinkStreamPredef.{StreamExecutionEnvironmentPredef, StreamFactory}
import ru.vtb.uasp.common.base.EnrichFlinkDataStream.EnrichFlinkDataStreamSink
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.extension.CommonExtension.Also
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.kafka.FlinkSinkProperties.producerFactoryDefault
import ru.vtb.uasp.common.service.JsonConvertOutService.IdentityPredef
import ru.vtb.uasp.common.service.UaspDeserializationProcessFunction
import ru.vtb.uasp.common.service.dto.KafkaDto
import ru.vtb.uasp.mdm.enrichment.service.JsValueConsumer
import ru.vtb.uasp.mdm.enrichment.service.dto.{FlinkDataStreams, KeyedCAData, KeyedUasp, OutStreams}
import ru.vtb.uasp.mdm.enrichment.utils.config.MDMEnrichmentPropsModel.appPrefixDefaultName
import ru.vtb.uasp.mdm.enrichment.utils.config._

object EnrichmentJob {

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
    logger.info("Start app: " + this.getClass.getName)
    try {
      val propsModel = MDMEnrichmentPropsModel.configApp(appPrefixDefaultName, args)

      val topic = propsModel.allEnrichProperty.commonEnrichProperty.get.dlqTopicProp
      val value = topic.get

      val env = StreamExecutionEnvironment.getExecutionEnvironment
      env.setParallelism(propsModel.appSyncParallelism)
      //      env.enableCheckpointing(propsModel.initProperty.appStreamCheckpointTimeMilliseconds.value, CheckpointingMode.EXACTLY_ONCE)


      val flinkDataStreams = init(env, propsModel)

      val mainDataStream = process(
        flinkDataStreams = flinkDataStreams,
        propsModel)

      setSinks(mainDataStream, propsModel)

      env.execute(propsModel.appServiceName)

    } catch {
      case e: Exception =>
        e.printStackTrace()
        logger.error("Error:" + e.getMessage)
        System.exit(1)
    }
  }

  def init(env: StreamExecutionEnvironment, propsModel: MDMEnrichmentPropsModel, sinkFunction: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault): FlinkDataStreams = {

    // получение продьюссеров для ошибок десериализации
    val producerDlqMain = propsModel.allEnrichProperty.mainEnrichProperty.dlqTopicProp.map(p => sinkFunction(p))
    val producerDlqCommon = propsModel.flinkSinkPropertiesCommonProducerDLQ.map(p => sinkFunction(p))
    val producerDlqGlobalId = propsModel.flinkSinkPropertiesGlobalIdProducerDLQ.map(p => sinkFunction(p))

    val serialisationProcessFunction = UaspDeserializationProcessFunction()

    val serialisationProcessFunctionJsValue = new JsValueConsumer()

    val mainDataStream = env
      .registerConsumer("mainData", propsModel.mainInputStream, producerDlqMain, serialisationProcessFunction)
    //      .map(u => EnrichmentUaspWithError(u.id, u, propsModel.allEnrichProperty.mainStreamProperty.alias, List()))


    val commonStream = propsModel.commonConsumer
      .map { cns =>
        env
          .registerConsumer(s"addSource-commonConsumer", cns, producerDlqCommon, serialisationProcessFunctionJsValue)
        //          .map(u => EnrichmentUaspWithError(u.id, u, propsModel.allEnrichProperty.commonEnrichPropertyWithAlias.get.alias, List()))
      }


    val globalIdStream = propsModel.globalIdConsumer
      .map { cns =>
        env.registerConsumer(s"addSource-globalIdConsumer", cns, producerDlqGlobalId, serialisationProcessFunctionJsValue)
        //          .map(u => EnrichmentUaspWithError(u.id, u, propsModel.allEnrichProperty.globalIdStreamPropertyWithAlias.get.alias, List()))
      }

    FlinkDataStreams(mainDataStream, commonStream, globalIdStream)
  }

  /**
   * Заставили под пытками сделать подобный метод, даже боюсь словами описывать его логику.
   */
  def process(flinkDataStreams: FlinkDataStreams,
              mDMEnrichmentPropsModel: MDMEnrichmentPropsModel,
              producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
             ): OutStreams = {

    val mainDlqSink = mDMEnrichmentPropsModel.allEnrichProperty.mainEnrichProperty.dlqTopicProp.map(p => p.createSinkFunction(producerFabric))

    val streamGlobal = flinkDataStreams.mainDataStream
      // если настроено обогащение глобальнымнтификатором, то надо обогатить
      .also { mainDs =>
        val maybeGlobal = for {
          keyedMainStreamSrv <- mDMEnrichmentPropsModel.globalMainStreamExtractKeyFunction
          keyGlobalSrv <- mDMEnrichmentPropsModel.keyGlobalIdEnrichmentMapService
          validateGlobalIdService <- mDMEnrichmentPropsModel.validateGlobalIdService
          globalIdStream <- flinkDataStreams.globalIdStream
        } yield (keyedMainStreamSrv, validateGlobalIdService, keyGlobalSrv, globalIdStream)
        maybeGlobal
          .map { tuple =>
            val (keyedMainStreamSrv, validateGlobalIdService, keyGlobalSrv, globalIdStream) = tuple
            val dlqGlobalIdSink: Option[SinkFunction[KafkaDto]] = mDMEnrichmentPropsModel.flinkSinkPropertiesCommonProducerDLQ.map(f => producerFabric(f))

            val validatedGlobalIdStream = globalIdStream
              .processAndDlqSink("validatedGlobalId", validateGlobalIdService, dlqGlobalIdSink)

            mainDs
              .processAndDlqSink(keyedMainStreamSrv, mainDlqSink)
              .keyBy(keySelectorMain)
              .connect(validatedGlobalIdStream.keyBy(d => d.key))
              .process(keyGlobalSrv)
              .processAndDlqSink("globalIdEnrich", mDMEnrichmentPropsModel.throwToDlqService, mainDlqSink)
          }.getOrElse(mainDs)
      }
    val streamCommon = streamGlobal
      // если настроено обогащение, то надо обогатаить. Тут меняются только мапки, поле id Не трогается
      .also { mainDs =>
        val maybeCommon = for {
          keyedMainStreamSrv <- mDMEnrichmentPropsModel.commonMainStreamExtractKeyFunction
          keyCommonEnrichmentMapService <- mDMEnrichmentPropsModel.keyCommonEnrichmentMapService
          commonValidateProcessFunction <- mDMEnrichmentPropsModel.commonValidateProcessFunction
          commonStream <- flinkDataStreams.commonStream
        } yield (keyedMainStreamSrv, keyCommonEnrichmentMapService, commonValidateProcessFunction, commonStream)

        maybeCommon
          .map { tuple =>
            val (keyedMainStreamSrv, keyCommonEnrichmentMapService, commonValidateProcessFunction, commonStream) = tuple
            val dlqGlobalIdSink: Option[SinkFunction[KafkaDto]] = keyCommonEnrichmentMapService.commonEnrichProperty.dlqTopicProp.map(prp => producerFabric(prp)
            )

            val validatedGlobalIdStream = commonStream
              .processAndDlqSink("validateCommonEnrich", commonValidateProcessFunction, dlqGlobalIdSink)

            mainDs
              .processAndDlqSink(keyedMainStreamSrv, mainDlqSink)
              .keyBy(keySelectorMain)
              .connect(validatedGlobalIdStream.keyBy(d => d.key))
              .process(keyCommonEnrichmentMapService)
              .processAndDlqSink("commonEnrich", mDMEnrichmentPropsModel.throwToDlqService, mainDlqSink)
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
      .map(_.serializeToBytes)
      .addSink(mainProducer)
      .enrichName(s"MAIN_SINK_outEnrichmentSink")
  }


}
