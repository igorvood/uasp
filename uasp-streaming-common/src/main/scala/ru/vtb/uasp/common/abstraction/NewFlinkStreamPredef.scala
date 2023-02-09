package ru.vtb.uasp.common.abstraction

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.DataStream
import play.api.libs.json.OWrites
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.mask.dto.JsMaskedPath
import ru.vtb.uasp.common.service.JsonConvertOutService.JsonPredef
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
                                                                             maskedRule: Option[JsMaskedPath]
                                                                         )(implicit oWrites: OWrites[OutDtoWithErrors[IN]]): DataStream[OUT] = {

    val myBeDlq = self
      .process[OUT](process)

    sinkDlqFunction
      .foreach{sf => {
        val value1 = myBeDlq
          .getSideOutput(process.dlqOutPut)
        val dlqStream = value1
          .map { d: OutDtoWithErrors[IN] => {
            val errorsOrDto = d.serializeToBytes(maskedRule)
             errorsOrDto match {
              case Left(value) => new OutDtoWithErrors[IN](
                serviceDataDto = serviceData,
                errorPosition = Some(this.getClass.getName),
                errors = value.map(q => q.error) ::: List("Error when try masked value"),
                data = None)
                .serializeToBytes
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
