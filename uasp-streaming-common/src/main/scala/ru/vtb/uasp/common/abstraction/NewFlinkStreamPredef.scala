package ru.vtb.uasp.common.abstraction

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.DataStream
import play.api.libs.json.OWrites
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.mask.dto.{JsMaskedPath, JsMaskedPathError}
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors, ServiceDataDto}

object NewFlinkStreamPredef {

  def privateCreateProducerWithMetric[T: TypeInformation](self: DataStream[T],
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

  def createProducerWithMetric[IN: TypeInformation](self: DataStream[IN],
                                                          serviceData: ServiceDataDto,
                                                          sinkProperty: FlinkSinkProperties,
                                                          maskedFun:IN => Either[List[JsMaskedPathError], KafkaDto],
                                                          sinkDlqFunction: Option[(FlinkSinkProperties, OutDtoWithErrors[IN] => Either[List[JsMaskedPathError], KafkaDto])],
                                                          producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto],

                                                         ): DataStreamSink[KafkaDto] = {
//    sinkDlqFunction
//      .foreach(dlqProp =>
//        {
//          processAndDlqSinkWithMetric(
//            self = self,
//            serviceData = serviceData,
//            process = new DlqProcessFunction[IN, KafkaDto, OutDtoWithErrors[IN]]() {
//              override def processWithDlq(dto: IN): Either[OutDtoWithErrors[IN], KafkaDto] = {
//                val errorsOrIn = maskedFun(dto)
//                errorsOrIn match {
//                  case Left(value) => Left(OutDtoWithErrors[IN](
//                    serviceDataDto = serviceData,
//                    errorPosition = Some(this.getClass.getName),
//                    errors = value.map(d => d.error) ::: List("asd"),
//                    data = Some(dto)))
//                  case Right(value) => Right(value)
//                }
//              }
//
//            },
//            sinkDlqFunction = Some(dlqProp._1),
//            producerFactory = producerFactory,
//            abstractOutDtoWithErrorsSerializeService = new AbstractOutDtoWithErrorsMaskedSerializeService[IN](dlqProp._1.jsMaskedPath) {
//              override def convert(value: OutDtoWithErrors[IN], jsMaskedPath: Option[JsMaskedPath]): Either[List[JsMaskedPathError], KafkaDto] = dlqProp._2(value)
//            }
//          )
//        }
//      )



    ???
  }

  def processAndDlqSinkWithMetric[IN: TypeInformation, OUT: TypeInformation](self: DataStream[IN],
                                                                             serviceData: ServiceDataDto,
                                                                             process: DlqProcessFunction[IN, OUT, OutDtoWithErrors[IN]],
                                                                             sinkDlqFunction: Option[(FlinkSinkProperties, OutDtoWithErrors[IN] => Either[List[JsMaskedPathError], KafkaDto])],
                                                                             producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto],
//                                                                             abstractOutDtoWithErrorsSerializeService: AbstractOutDtoWithErrorsMaskedSerializeService[IN]
                                                                            ): DataStream[OUT] = {




    val myBeDlq = self
      .process[OUT](process)
    sinkDlqFunction
      .foreach { sf => {
        val maskedDLQSerializeService: AbstractOutDtoWithErrorsMaskedSerializeService[IN] = new AbstractOutDtoWithErrorsMaskedSerializeService[IN](sf._1.jsMaskedPath) {
          override def convert(value: OutDtoWithErrors[IN], jsMaskedPath: Option[JsMaskedPath]): Either[List[JsMaskedPathError], KafkaDto] = sf._2(value)
        }



        val value1 = myBeDlq
          .getSideOutput(process.dlqOutPut)
        val dlqStream = value1
          .map { d: OutDtoWithErrors[IN] => {
            val errorsOrDto: Either[List[JsMaskedPathError], KafkaDto] = maskedDLQSerializeService.map(d)
            errorsOrDto match {
              case Left(value) =>
                val value2 = new OutDtoWithErrors[IN](
                  serviceDataDto = serviceData,
                  errorPosition = Some(this.getClass.getName),
                  errors = value.map(q => q.error) ::: List("Error when try masked value"),
                  data = None)
                maskedDLQSerializeService.map(value2).right.get
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
