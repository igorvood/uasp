package ru.vtb.uasp.common.abstraction

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.DataStream
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.mask.dto.{JsMaskedPath, JsMaskedPathError}
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors, ServiceDataDto}

object NewFlinkStreamPredef {

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
                                                                    sinkProperty: FlinkSinkProperties,
                                                                    maskedFun: IN => Either[List[JsMaskedPathError], KafkaDto],
                                                                    sinkDlqProperty: Option[(FlinkSinkProperties, OutDtoWithErrors[IN] => Either[List[JsMaskedPathError], KafkaDto])],
                                                                    producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto],
                                                                   ): DataStreamSink[KafkaDto] = {

    val dlqProcessFunction = new DlqProcessFunction[IN, KafkaDto, OutDtoWithErrors[IN]] {
      override def processWithDlq(dto: IN): Either[OutDtoWithErrors[IN], KafkaDto] = {
        val errorsOrDto = maskedFun(dto)

        val either = errorsOrDto match {
          case Left(value) => Left(OutDtoWithErrors[IN](serviceData, Some(this.getClass.getName), value.map(q => q.error) ::: List("asdsad"), Some(dto)))
          case Right(value) => Right(value)
        }

        either
      }
    }


    val maskedDataStream = processAndDlqSinkWithMetric(self, serviceData, dlqProcessFunction, sinkDlqProperty, producerFactory)

    privateCreateProducerWithMetric(maskedDataStream, serviceData, sinkProperty, producerFactory)

  }

  private[common] def processAndDlqSinkWithMetric[IN: TypeInformation, OUT: TypeInformation](self: DataStream[IN],
                                                                                             serviceData: ServiceDataDto,
                                                                                             process: DlqProcessFunction[IN, OUT, OutDtoWithErrors[IN]],
                                                                                             sinkDlqProperty: Option[(FlinkSinkProperties, OutDtoWithErrors[IN] => Either[List[JsMaskedPathError], KafkaDto])],
                                                                                             producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto],
                                                                                            ): DataStream[OUT] = {
    val myBeDlq = self
      .process[OUT](process)
    sinkDlqProperty
      .foreach { sf => {
        val maskedDLQSerializeService: AbstractOutDtoWithErrorsMaskedSerializeService[IN] = new AbstractOutDtoWithErrorsMaskedSerializeService[IN](sf._1.jsMaskedPath) {

          override def convert(value: OutDtoWithErrors[IN], jsMaskedPath: Option[JsMaskedPath]): Either[List[JsMaskedPathError], KafkaDto] = sf._2(value)
        }


        val dlq = myBeDlq
          .getSideOutput(process.dlqOutPut)
        val dlqStream = dlq
          .map { d: OutDtoWithErrors[IN] => {
            val errorsOrDto: Either[List[JsMaskedPathError], KafkaDto] = maskedDLQSerializeService.convert(d, maskedDLQSerializeService.jsMaskedPath)
            errorsOrDto match {
              case Left(value) =>
                val value2 = new OutDtoWithErrors[IN](
                  serviceDataDto = serviceData,
                  errorPosition = Some(this.getClass.getName),
                  errors = value.map(q => q.error) ::: List("Error when try masked value"),
                  data = None)
                maskedDLQSerializeService.convert(value2, maskedDLQSerializeService.jsMaskedPath).right.get
              case Right(masked) => masked
            }
          }

          }
        privateCreateProducerWithMetric(dlqStream, serviceData, sf._1, producerFactory)
      }


      }

    myBeDlq
  }

}
