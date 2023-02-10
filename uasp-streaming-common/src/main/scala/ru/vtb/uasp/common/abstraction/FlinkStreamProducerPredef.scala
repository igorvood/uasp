package ru.vtb.uasp.common.abstraction

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.datastream.DataStreamSink
//import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.DataStream
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.mask.dto.JsMaskedPathError
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors, ServiceDataDto}

object FlinkStreamProducerPredef {


  implicit class StreamFactory[IN](val self: DataStream[IN]) extends AnyVal {
    def processWithMaskedDqlF[OUT: TypeInformation](serviceData: ServiceDataDto,
                                                    process: DlqProcessFunction[IN, OUT, OutDtoWithErrors[IN]],
                                                    sinkDlqProperty: Option[(FlinkSinkProperties, OutDtoWithErrors[IN] => Either[List[JsMaskedPathError], KafkaDto])],
                                                    producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto],
                                                   ): DataStream[OUT] = {
      implicit val typeInformationIn: TypeInformation[IN] = self.dataType
      NewFlinkStreamPredef.processAndDlqSinkWithMetric(self, serviceData, process, sinkDlqProperty, producerFactory)
    }

    def processWithMaskedDql[OUT: TypeInformation](serviceData: ServiceDataDto,
                                                   process: DlqProcessFunction[IN, OUT, OutDtoWithErrors[IN]],
                                                   sinkDlqProperty: Option[(FlinkSinkProperties, AbstractOutDtoWithErrorsMaskedSerializeService[IN])],
                                                   producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto],
                                                  ): DataStream[OUT] = {


      val maybeProperties: Option[(FlinkSinkProperties, OutDtoWithErrors[IN] => Either[List[JsMaskedPathError], KafkaDto])] = abstractOutDtoToFun(sinkDlqProperty)

      processWithMaskedDqlF(serviceData, process, maybeProperties, producerFactory)
    }

    def maskedProducerF(serviceData: ServiceDataDto,
                       sinkProperty: FlinkSinkProperties,
                       maskedFun: IN => Either[List[JsMaskedPathError], KafkaDto],
                       sinkDlqProperty: Option[(FlinkSinkProperties, OutDtoWithErrors[IN] => Either[List[JsMaskedPathError], KafkaDto])],
                       producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto],
                      ): DataStreamSink[KafkaDto] = {
      implicit val typeInformationIn: TypeInformation[IN] = self.dataType
      NewFlinkStreamPredef.createProducerWithMetric(
        self,
        serviceData,
        sinkProperty,
        maskedFun,
        sinkDlqProperty,
        producerFactory
      )
    }

    def maskedProducer(serviceData: ServiceDataDto,
                        sinkProperty: FlinkSinkProperties,
                        maskedFun: IN => Either[List[JsMaskedPathError], KafkaDto],
                        sinkDlqProperty: Option[(FlinkSinkProperties, AbstractOutDtoWithErrorsMaskedSerializeService[IN])],
                        producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto],
                       ): DataStreamSink[KafkaDto] = {

      val maybeTuple = abstractOutDtoToFun(sinkDlqProperty)
      maskedProducerF(
        serviceData,
        sinkProperty,
        maskedFun,
        maybeTuple,
        producerFactory
      )
    }

  }

  private def abstractOutDtoToFun[IN](property: Option[(FlinkSinkProperties, AbstractOutDtoWithErrorsMaskedSerializeService[IN])]) = {
    val maybeProperties: Option[(FlinkSinkProperties, OutDtoWithErrors[IN] => Either[List[JsMaskedPathError], KafkaDto])] = for {
      dlqProperty <- property
      p = dlqProperty._1
      q = { s: OutDtoWithErrors[IN] =>
        dlqProperty._2.convert(s, p.jsMaskedPath)
      }
    } yield (p, q)
    maybeProperties
  }
}
