package ru.vtb.uasp.common.abstraction

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.DataStream
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.mask.dto.JsMaskedPathError
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors, ServiceDataDto}

object NewFlinkStreamPredef {

  def createProducerWithMetric[T: TypeInformation](self: DataStream[T],
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


  def processAndDlqSinkWithMetric[IN: TypeInformation, OUT: TypeInformation](self: DataStream[IN],
                                                                             serviceData: ServiceDataDto,
                                                                             process: DlqProcessFunction[IN, OUT, OutDtoWithErrors[IN]],
                                                                             sinkDlqFunction: Option[FlinkSinkProperties],
                                                                             producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto],
                                                                             abstractOutDtoWithErrorsSerializeService: AbstractOutDtoWithErrorsMaskedSerializeService[IN]
                                                                            ): DataStream[OUT] = {

    val myBeDlq = self
      .process[OUT](process)

    sinkDlqFunction
      .foreach { sf => {
        val value1 = myBeDlq
          .getSideOutput(process.dlqOutPut)
        val dlqStream = value1
          .map { d: OutDtoWithErrors[IN] => {
            val errorsOrDto: Either[List[JsMaskedPathError], KafkaDto] = abstractOutDtoWithErrorsSerializeService.map(d)
            errorsOrDto match {
              case Left(value) =>
                val value2 = new OutDtoWithErrors[IN](
                  serviceDataDto = serviceData,
                  errorPosition = Some(this.getClass.getName),
                  errors = value.map(q => q.error) ::: List("Error when try masked value"),
                  data = None)
                abstractOutDtoWithErrorsSerializeService.map(value2).right.get
              case Right(masked) => masked
            }
          }

          }
        createProducerWithMetric(dlqStream, serviceData, sf, producerFactory)
      }


      }

    myBeDlq
  }

}
