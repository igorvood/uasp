package ru.vtb.uasp.mdm.enrichment.service

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.DataStream
import play.api.libs.json.JsValue
import ru.vtb.uasp.common.abstraction.FlinkStreamProducerPredef.StreamFactory
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.mask.dto.{JsMaskedPath, JsMaskedPathError}
import ru.vtb.uasp.common.service.JsonConvertOutService.JsonPredef
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors, PropertyWithSerializer, ServiceDataDto}
import ru.vtb.uasp.mdm.enrichment.service.dto.{KeyedCAData, NotStandardDataStreams, StandartedDataStreams}

class TransformSecondaryStreamService(
                                       private val serviceData: ServiceDataDto,
                                       private val globalIdValidateService: Option[ExtractKeyFunction],
                                       private val commonValidateProcessFunction: Option[ExtractKeyFunction],
                                       private val dlqGlobalIdProp: Option[FlinkSinkProperties],
                                       private val dlqCommonProp: Option[FlinkSinkProperties],
                                     ) {


  def transform(streams: NotStandardDataStreams)(implicit producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto]): StandartedDataStreams = {

    val transformedGlobalIdStream = transformOneStream(
      streams.globalIdStream,
      globalIdValidateService,
      dlqGlobalIdProp.map(sp => PropertyWithSerializer(sp, {_.serializeToKafkaJsValue}))
    )

    val transformedCommonStream = transformOneStream(
      streams.commonStream,
      commonValidateProcessFunction,
      dlqCommonProp.map(sp => PropertyWithSerializer(sp, {_.serializeToKafkaJsValue}))
    )

    StandartedDataStreams(streams.mainDataStream, transformedCommonStream, transformedGlobalIdStream)

  }

  private def transformOneStream(
                                  streamForTransform: Option[DataStream[JsValue]],
                                  validateService: Option[ExtractKeyFunction],
//                                  dlq: Option[(FlinkSinkProperties, (OutDtoWithErrors[JsValue], Option[JsMaskedPath]) => Either[List[JsMaskedPathError], KafkaDto])]
                                  dlq: Option[PropertyWithSerializer[OutDtoWithErrors[JsValue]]],
                                )(implicit producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto]): Option[DataStream[KeyedCAData]] = {
    for {
      ds <- streamForTransform
      validService <- validateService
    } yield ds.processWithMaskedDqlF(
      serviceData,
      validService,
      dlq,
      producerFabric)
  }
}
