package ru.vtb.uasp.common.abstraction

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import ru.vtb.uasp.common.kafka.FlinkConsumerProperties
import ru.vtb.uasp.common.mask.dto.JsMaskedPath
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.DataStream
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.mask.dto.JsMaskedPathError
import ru.vtb.uasp.common.service.JsonConvertOutService.JsonPredef
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors, ServiceDataDto}

object FlinkStreamProducerPredef {


  implicit class StreamFactory[IN](val self: DataStream[IN]) extends AnyVal {
    def processWithMaskedDqlF[OUT: TypeInformation](serviceData: ServiceDataDto,
                                                    process: DlqProcessFunction[IN, OUT, OutDtoWithErrors[IN]],
                                                    sinkDlqProperty: Option[(FlinkSinkProperties, (OutDtoWithErrors[IN], Option[JsMaskedPath]) => Either[List[JsMaskedPathError], KafkaDto])],
                                                    producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto],
                                                   ): DataStream[OUT] = {
      implicit val typeInformationIn: TypeInformation[IN] = self.dataType
      FlinkKafkaFun.processAndDlqSinkWithMetric(self, serviceData, process, sinkDlqProperty, producerFactory)
    }

    @deprecated("use processWithMaskedDqlF")
    def processWithMaskedDql[OUT: TypeInformation](serviceData: ServiceDataDto,
                                                   process: DlqProcessFunction[IN, OUT, OutDtoWithErrors[IN]],
                                                   sinkDlqProperty: Option[(FlinkSinkProperties, AbstractOutDtoWithErrorsMaskedSerializeService[IN])],
                                                   producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto],
                                                  ): DataStream[OUT] = {


      val maybeProperties: Option[(FlinkSinkProperties, (OutDtoWithErrors[IN], Option[JsMaskedPath]) => Either[List[JsMaskedPathError], KafkaDto])] = abstractOutDtoToFun(sinkDlqProperty)

      processWithMaskedDqlF(serviceData, process, maybeProperties, producerFactory)
    }

    def maskedProducerF(serviceData: ServiceDataDto,
                        sinkProperty: FlinkSinkProperties,
                        maskedFun: (IN, Option[JsMaskedPath]) => Either[List[JsMaskedPathError], KafkaDto],
                        sinkDlqProperty: Option[(FlinkSinkProperties, (OutDtoWithErrors[IN], Option[JsMaskedPath]) => Either[List[JsMaskedPathError], KafkaDto])],
                        producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto],
                       ): DataStreamSink[KafkaDto] = {
      implicit val typeInformationIn: TypeInformation[IN] = self.dataType
      FlinkKafkaFun.createProducerWithMetric(
        self,
        serviceData,
        sinkProperty,
        maskedFun,
        sinkDlqProperty,
        producerFactory
      )
    }

    @deprecated("use maskedProducerF")
    def maskedProducer(serviceData: ServiceDataDto,
                       sinkProperty: FlinkSinkProperties,
                       maskedFun: AbstractDtoMaskedSerializeService[IN],
                       sinkDlqProperty: Option[(FlinkSinkProperties, AbstractOutDtoWithErrorsMaskedSerializeService[IN])],
                       producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto],
                      ): DataStreamSink[KafkaDto] = {

      val maybeTuple = abstractOutDtoToFun(sinkDlqProperty)

      maskedProducerF(
        serviceData,
        sinkProperty,
        maskedFun.convert,
        maybeTuple,
        producerFactory
      )
    }

  }

  implicit class StreamExecutionEnvironmentPredef(val self: StreamExecutionEnvironment) extends AnyVal {

//    def registerConsumerWithMetric[O: TypeInformation](
//                                                        serviceData: ServiceDataDto,
//                                                        consumerProperties: FlinkConsumerProperties,
//                                                        sinkDlqProperty: Option[(FlinkSinkProperties, (OutDtoWithErrors[Nothing], Option[JsMaskedPath]) => Either[List[JsMaskedPathError], KafkaDto])],
//                                                        serialisationProcessFunction: DlqProcessFunction[Array[Byte], O, OutDtoWithErrors[Nothing]],
//                                                        producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto]
//                                                      ): DataStream[O] = {
//      val consumer = consumerProperties.createConsumer()
//
//      val value = self.addSource(consumer)
//        .map(consumerProperties.prometheusMetric[Array[Byte]](serviceData))
//
//      val value1 = value
//        .process(serialisationProcessFunction)
//
//      sinkDlqProperty.foreach(dProp => {
//        val value2 = value1
//          .getSideOutput(serialisationProcessFunction.dlqOutPut)
//          .maskedProducerF(serviceData, dProp._1, dProp._2, None, producerFactory)
//
//      }
//      )
//      value1
//
//    }

    def registerConsumerWithMetric1[O: TypeInformation](
                                                         serviceData: ServiceDataDto,
                                                         consumerProperties: FlinkConsumerProperties,
                                                         dlqProducer: Option[FlinkSinkProperties],
                                                         serialisationProcessFunction: DlqProcessFunction[Array[Byte], O, JsMaskedPathError],
                                                         producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto]
                                                       ): DataStream[O] = {
      val consumer = consumerProperties.createConsumer()


      val mainStream = self.addSource(consumer)
        .process(serialisationProcessFunction)


      dlqProducer.map { dlq =>
        val errorStream = mainStream
          .getSideOutput(serialisationProcessFunction.dlqOutPut)
        errorStream.maskedProducerF(
          serviceData = serviceData,
          dlq,

          { (a, s) => a.serializeToBytes(s) },
          sinkDlqProperty = Some(dlq, { (a, s) => {

            val errs = a.data.map(m =>
              m.error :: a.errors)
              .getOrElse(a.errors)
            a.copy[JsMaskedPathError](errors = errs, data = None).serializeToBytes(s)
          }
          }),
          producerFactory = producerFactory
        )


      }
      mainStream
    }
  }

  private def abstractOutDtoToFun[IN](property: Option[(FlinkSinkProperties, AbstractOutDtoWithErrorsMaskedSerializeService[IN])]): Option[(FlinkSinkProperties, (OutDtoWithErrors[IN], Option[JsMaskedPath]) => Either[List[JsMaskedPathError], KafkaDto])] = {
    val maybeTuple = for {
      dlqProperty <- property
      p = dlqProperty._1
      q = { (s: OutDtoWithErrors[IN], f: Option[JsMaskedPath]) =>
        dlqProperty._2.convert(s, f)
      }
    } yield (p, q)
    val maybeProperties: Option[(FlinkSinkProperties, (OutDtoWithErrors[IN], Option[JsMaskedPath]) => Either[List[JsMaskedPathError], KafkaDto])] = maybeTuple
    maybeProperties
  }
}
