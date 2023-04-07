package ru.vtb.uasp.common.abstraction

import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.DataStream
import play.api.libs.json.Json
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.mask.MaskedPredef.MaskJsValuePredef
import ru.vtb.uasp.common.mask.dto.JsMaskedPathError
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors, PropertyWithSerializer, ServiceDataDto}

object FlinkKafkaFun {

  private[common] def privateCreateProducerWithMetric[T: TypeInformation](self: DataStream[T],
                                                                          serviceData: ServiceDataDto,
                                                                          sinkProperty: FlinkSinkProperties,
                                                                          producerFactory: FlinkSinkProperties => SinkFunction[T]
                                                                         ): DataStreamSink[T] = {
    val metricsFunction = sinkProperty.prometheusMetric[T](serviceData)
    val value = self
      .map[T](metricsFunction)
      .addSink(sinkProperty.createSinkFunction(producerFactory))
    value
  }

  private[common] def createProducerWithMetric[IN: TypeInformation](self: DataStream[IN],
                                                                    serviceData: ServiceDataDto,
                                                                    sinkPropertyWithSerializer: PropertyWithSerializer[IN],
                                                                    sinkDlqPropertyWithSerializer: Option[PropertyWithSerializer[OutDtoWithErrors[IN]]],
                                                                    producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto],
                                                                   ): DataStreamSink[KafkaDto] = {

    val dlqProcessFunction = new DlqProcessFunction[IN, KafkaDto, OutDtoWithErrors[IN]] {
      override def processWithDlq(dto: IN): Either[OutDtoWithErrors[IN], KafkaDto] = {

        val kafkaJsValueDto = sinkPropertyWithSerializer.serializerToKafkaJsValue(dto)
        val errorsOrDto: Either[List[JsMaskedPathError], KafkaDto] = sinkPropertyWithSerializer.flinkSinkProperties.jsMaskedPath
          .map(mf => kafkaJsValueDto.jsValue.toMaskedJson(mf))
          .getOrElse(Right(kafkaJsValueDto.jsValue))
          .map(s => KafkaDto(kafkaJsValueDto.id.getBytes(), Json.stringify(s).getBytes()))

        val either: Either[OutDtoWithErrors[IN], KafkaDto] = errorsOrDto match {
          case Left(value) => Left(OutDtoWithErrors[IN](
            serviceDataDto = serviceData,
            errorPosition = Some(this.getClass.getName),
            errors = s"createProducerWithMetric: Unable to mask dto ${dto.getClass.getName}. Masked rule ${sinkPropertyWithSerializer.flinkSinkProperties.jsMaskedPath}" :: value.map(q => q.error),
            data = Some(dto))
          )
          case Right(value) => Right(value)
        }

        either
      }
    }


    val maskedDataStream = processAndDlqSinkWithMetric(self, serviceData, dlqProcessFunction, sinkDlqPropertyWithSerializer, producerFactory)

    privateCreateProducerWithMetric(maskedDataStream, serviceData, sinkPropertyWithSerializer.flinkSinkProperties, producerFactory)

  }

  private[common] def processAndDlqSinkWithMetric[IN: TypeInformation, OUT: TypeInformation](self: DataStream[IN],
                                                                                             serviceData: ServiceDataDto,
                                                                                             process: AbstractDlqProcessFunction[IN, OUT, OutDtoWithErrors[IN]],
                                                                                             sinkDlqPropertyWithSerializer: Option[PropertyWithSerializer[OutDtoWithErrors[IN]]],
                                                                                             producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto],
                                                                                            ): DataStream[OUT] = {
    val myBeDlq = self
      .process[OUT](process)

    sinkDlqPropertyWithSerializer
      .foreach { sf =>
        val value1: RichMapFunction[OutDtoWithErrors[IN], KafkaDto] = new RichMapFunction[OutDtoWithErrors[IN], KafkaDto] {
          override def map(value: OutDtoWithErrors[IN]): KafkaDto = {

            val kafkaJsValueDto = sf.serializerToKafkaJsValue(value)
            val errorsOrDto = sf.flinkSinkProperties.jsMaskedPath
              .map(mf => kafkaJsValueDto.jsValue.toMaskedJson(mf))
              .getOrElse(Right(kafkaJsValueDto.jsValue))
              .map(s => KafkaDto(kafkaJsValueDto.id.getBytes(), Json.stringify(s).getBytes()))

            {
              errorsOrDto match {
                case Left(value) =>
                  val value2 = new OutDtoWithErrors[IN](
                    serviceDataDto = serviceData,
                    errorPosition = Some(this.getClass.getName),
                    errors = s"processAndDlqSinkWithMetric: Unable to mask dto ${value.getClass.getName}. Masked rule ${sf.flinkSinkProperties.jsMaskedPath}" :: value.map(q => q.error),
                    data = None)
                  val dto = sf.serializerToKafkaJsValue(value2)
                  KafkaDto(dto.id.getBytes(), Json.stringify(dto.jsValue).getBytes())
                case Right(masked) => masked
              }
            }
          }
        }

        val dlq = myBeDlq
          .getSideOutput(process.dlqOutPut)
        val dlqStream = dlq
          .map(value1)
        privateCreateProducerWithMetric(dlqStream, serviceData, sf.flinkSinkProperties, producerFactory)
      }
    myBeDlq
  }

}
