package ru.vtb.uasp.mdm.enrichment

import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}
import org.slf4j.LoggerFactory
import play.api.libs.json.{JsObject, JsValue, Json, OWrites, Writes}
import ru.vtb.uasp.common.abstraction.DlqProcessFunction
import ru.vtb.uasp.common.abstraction.FlinkStreamProducerPredef.StreamExecutionEnvironmentPredef
import ru.vtb.uasp.common.abstraction.FlinkStreamProducerPredef.StreamFactory
import ru.vtb.uasp.common.base.EnrichFlinkDataStream.EnrichFlinkDataStreamSink
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.extension.CommonExtension.Also
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.kafka.FlinkSinkProperties.producerFactoryDefault
import ru.vtb.uasp.common.mask.dto.JsMaskedPathError
import ru.vtb.uasp.common.service.JsonConvertOutService.{IdentityPredef, JsonPredef}
import ru.vtb.uasp.common.service.UaspDeserializationProcessFunction
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors}
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

      val env = StreamExecutionEnvironment.getExecutionEnvironment
      env.setParallelism(propsModel.appSyncParallelism)
      //      env.enableCheckpointing(propsModel.initProperty.appStreamCheckpointTimeMilliseconds.value, CheckpointingMode.EXACTLY_ONCE)


      val flinkDataStreams = init(env, propsModel)

      val mainDataStream = process(
        flinkDataStreams = flinkDataStreams,
        propsModel)

      setSinks(mainDataStream, propsModel)

      env.execute(propsModel.serviceData.fullServiceName)

    } catch {
      case e: Exception =>
        e.printStackTrace()
        logger.error("Error:" + e.getMessage)
        System.exit(1)
    }
  }

  def init(env: StreamExecutionEnvironment, propsModel: MDMEnrichmentPropsModel, producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault): FlinkDataStreams = {


    implicit val serviceData = propsModel.serviceData

    val serialisationProcessFunction = UaspDeserializationProcessFunction()

    val value = new DlqProcessFunction[Array[Byte], UaspDto, JsMaskedPathError]() {
      override def processWithDlq(dto: Array[Byte]): Either[JsMaskedPathError, UaspDto] = ???
    }

    val serialisationProcessFunctionJsValue = new JsValueConsumer()

    val mainDataStream = env
      .registerConsumerWithMetric1(
        propsModel.serviceData,
        propsModel.allEnrichProperty.mainEnrichProperty.fromTopic,
        propsModel.allEnrichProperty.mainEnrichProperty.dlqTopicProp,
        value,
        producerFabric)


    val commonStream = propsModel.allEnrichProperty.commonEnrichProperty
      .map { cns =>
        val commonEnrichPropertyDlq = propsModel.allEnrichProperty.commonEnrichProperty.flatMap(a => a.dlqTopicProp)
        env
          .registerConsumerWithMetric1(
            propsModel.serviceData,
            cns.fromTopic,
            commonEnrichPropertyDlq, serialisationProcessFunctionJsValue, producerFabric)
      }


    val globalIdStream = propsModel.allEnrichProperty.globalIdEnrichProperty
      .map { cns =>
        val globalIdEnrichPropertyDlq = propsModel.allEnrichProperty.globalIdEnrichProperty.flatMap(a => a.dlqTopicProp)
        env.registerConsumerWithMetric1(
          propsModel.serviceData,
          cns.fromTopic,
          globalIdEnrichPropertyDlq,
          serialisationProcessFunctionJsValue,
          producerFabric)
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

    implicit val value1 = Json.writes[JsValue]
    implicit val value11 = ru.vtb.uasp.common.service.dto.OutDtoWithErrors.outDtoWithErrorsJsonWrites[JsValue]

//    implicit val value = Json.writes[Right[(UaspDto, String),UaspDto]]
//    implicit val value3 = Json.writes[Left[(UaspDto, String), UaspDto]]
//
//    implicit val value2 = Json.writes[Either[(UaspDto, String), UaspDto]]


    implicit  val writes: OWrites[Either[(UaspDto, String), UaspDto]] = new OWrites[Either[(UaspDto, String), UaspDto]] {

      //    override def writes(o: When): JsValue = Json.obj(
      //      o.when.fold(
      //        duration => "duration" -> duration.getMillis,
      //        dateTime => "dateTime" -> dateTime.getMillis
      //      )
      //    )

      override def writes(o: Either[(UaspDto, String), UaspDto]): JsObject = Json.obj(
        o match {
          case Left(value) => "err"->value._2
          case Right(value) => "err" -> Json.toJsObject(value)
        }
      )
    }

//    implicit val value = OWrites.of(writes)

        implicit val value22 = ru.vtb.uasp.common.service.dto.OutDtoWithErrors.outDtoWithErrorsJsonWrites[Either[(UaspDto, String), UaspDto]](writes)

    val mainDlqProp = mDMEnrichmentPropsModel.allEnrichProperty.mainEnrichProperty.dlqTopicProp

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
            val dlqGlobalIdProp = mDMEnrichmentPropsModel.allEnrichProperty.globalIdEnrichProperty.flatMap(a => a.dlqTopicProp)

            val service = validateGlobalIdService
            val validatedGlobalIdStream = globalIdStream
              .processWithMaskedDqlF(
                mDMEnrichmentPropsModel.serviceData,
                service,
                dlqGlobalIdProp.map(sp => sp -> {(q,w) => q.serializeToBytes(w) }),
                producerFabric)

            val value2 = mainDs
              .processWithMaskedDqlF(
                mDMEnrichmentPropsModel.serviceData,
                keyedMainStreamSrv,
                mainDlqProp.map(sp => sp -> { (q, w) => q.serializeToBytes(w) }),
                producerFabric)
              .keyBy(keySelectorMain)
            val value3 = value2
              .connect(validatedGlobalIdStream.keyBy(d => d.key))
            val value: DataStream[Either[OutDtoWithErrors[UaspDto], UaspDto]] = value3
              .process(keyGlobalSrv)

            // TODO тут добвавить откидывание в dLQ
            val value4 = value.process(mDMEnrichmentPropsModel.throwToDlqService)
//            val value5 = value4
//              .getSideOutput(mDMEnrichmentPropsModel.throwToDlqService.dlqOutPut)
//              .maskedProducerF(
//                mDMEnrichmentPropsModel.serviceData,
//
//              )

//            value
//              .processWithMaskedDqlF(
//                mDMEnrichmentPropsModel.serviceData,
//                mDMEnrichmentPropsModel.throwToDlqService,
//                mainDlqProp.map(sp => sp -> {(q,w)=> q.serializeToBytes(w)}),
//                producerFabric)
            value4
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
            val dlqGlobalIdProp = mDMEnrichmentPropsModel.allEnrichProperty.commonEnrichProperty.flatMap(a => a.dlqTopicProp)

            val validatedGlobalIdStream = commonStream
              .processWithMaskedDqlF(
                mDMEnrichmentPropsModel.serviceData,
                commonValidateProcessFunction,
                dlqGlobalIdProp.map(sp => sp-> {(q,w)=> q.serializeToBytes(w)}), producerFabric)

            val value: DataStream[Either[OutDtoWithErrors[UaspDto], UaspDto]] = mainDs
              .processWithMaskedDqlF(
                mDMEnrichmentPropsModel.serviceData,
                keyedMainStreamSrv,
                mainDlqProp.map(sp => sp -> { (q, w) => q.serializeToBytes(w) }), producerFabric)
              .keyBy(keySelectorMain)
              .connect(validatedGlobalIdStream.keyBy(d => d.key))
              .process(keyCommonEnrichmentMapService)

            val value2: DlqProcessFunction[Either[OutDtoWithErrors[UaspDto], UaspDto], UaspDto, OutDtoWithErrors[UaspDto]] = new DlqProcessFunction[Either[OutDtoWithErrors[UaspDto], UaspDto], UaspDto, OutDtoWithErrors[UaspDto]] {
              override def processWithDlq(dto: Either[OutDtoWithErrors[UaspDto], UaspDto]): Either[OutDtoWithErrors[UaspDto], UaspDto] = dto
            }
            val value3 = value.processWithMaskedDql1[UaspDto, UaspDto](
              mDMEnrichmentPropsModel.serviceData,
              value2,
              mainDlqProp.map(sp => sp -> { (q, w) => q.serializeToBytes(w) }),
              producerFabric
            )
            value3
//            value
//              .processWithMaskedDqlF(
//                mDMEnrichmentPropsModel.serviceData,
//                mDMEnrichmentPropsModel.throwToDlqService,
//                mainDlqProp.map(sp => sp -> {(q,w)=>q.serializeToBytes(w)}), producerFabric)
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
      .map(syncProperties.flinkSinkPropertiesMainProducer.prometheusMetric[KafkaDto](syncProperties.serviceData))
      .addSink(mainProducer)
      .enrichName(s"MAIN_SINK_outEnrichmentSink")
  }


}
