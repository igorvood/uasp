package ru.vtb.uasp.common.abstraction

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import play.api.libs.json.{JsObject, JsValue, Json, OWrites}
import ru.vtb.uasp.common.kafka.{FlinkConsumerProperties, FlinkSinkProperties}
import ru.vtb.uasp.common.mask.MaskedPredef.MaskJsValuePredef
import ru.vtb.uasp.common.mask.dto.{JsMaskedPath, JsMaskedPathError}
import ru.vtb.uasp.common.service.JsonConvertOutService.{JsonPredef, serializeToBytes}
import ru.vtb.uasp.common.service.dto.{KafkaDto, KafkaJsValueDto, OutDtoWithErrors, PropertyWithSerializer, ServiceDataDto}

object FlinkStreamProducerPredef {

  implicit class StreamFactory[IN](val self: DataStream[IN]) extends AnyVal {
    def processWithMaskedDqlF[OUT: TypeInformation](serviceData: ServiceDataDto,
                                                    process: AbstractDlqProcessFunction[IN, OUT, OutDtoWithErrors[IN]],
                                                    sinkDlqProperty: Option[(FlinkSinkProperties, (OutDtoWithErrors[IN], Option[JsMaskedPath]) => Either[List[JsMaskedPathError], KafkaDto])],
                                                    producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto],
                                                   ): DataStream[OUT] = {
      implicit val typeInformationIn: TypeInformation[IN] = self.dataType
      FlinkKafkaFun.processAndDlqSinkWithMetric(self, serviceData, process, sinkDlqProperty, producerFactory)
    }

    def processWithMaskedDqlFC[OUT: TypeInformation, DLQ: TypeInformation](serviceData: ServiceDataDto,
                                                                           process: AbstractDlqProcessFunction[IN, OUT, OutDtoWithErrors[DLQ]],
                                                                           sinkDlqProperty: Option[(FlinkSinkProperties, (OutDtoWithErrors[DLQ], Option[JsMaskedPath]) => Either[List[JsMaskedPathError], KafkaDto])],
                                                                           producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto],
                                                                          ): DataStream[OUT] = {
      implicit val typeInformationIn: TypeInformation[IN] = self.dataType

      val okStream = self.process(process)

      sinkDlqProperty
        .foreach(d => {

          val flinkSinkProperties = d._1
          val mf: (OutDtoWithErrors[DLQ], Option[JsMaskedPath]) => Either[List[JsMaskedPathError], KafkaDto] = d._2

          val errStream: DataStream[KafkaDto] = okStream
            .getSideOutput(process.dlqOutPut)
            .map(err => mf(err, flinkSinkProperties.jsMaskedPath))
            .map(a => a match {
              case Left(value) =>
                val strings: List[String] = "Masked error" :: value.map(_.error)
                mf(OutDtoWithErrors[DLQ](
                  serviceData,
                  Some("processWithMaskedDql1"),
                  strings,
                  None), flinkSinkProperties.jsMaskedPath).right.get
              case Right(value) => value
            }
            )
          FlinkKafkaFun.privateCreateProducerWithMetric(
            errStream,
            serviceData,
            flinkSinkProperties,
            producerFactory
          )
        }
        )
      okStream
    }

    @deprecated("use processWithMaskedDqlF")
    private [common] def processWithMaskedDql[OUT: TypeInformation](serviceData: ServiceDataDto,
                                                   process: AbstractDlqProcessFunction[IN, OUT, OutDtoWithErrors[IN]],
                                                   sinkDlqProperty: Option[(FlinkSinkProperties, AbstractOutDtoWithErrorsMaskedSerializeService[IN])],
                                                   producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto],
                                                  ): DataStream[OUT] = {


      val maybeProperties: Option[(FlinkSinkProperties, (OutDtoWithErrors[IN], Option[JsMaskedPath]) => Either[List[JsMaskedPathError], KafkaDto])] = abstractOutDtoToFun(sinkDlqProperty)

      processWithMaskedDqlF(serviceData, process, maybeProperties, producerFactory)
    }


    def maskedProducerFErr(serviceData: ServiceDataDto,
                           dlqSinkPropertyWithSerializer:                            PropertyWithSerializer[IN],
//                           dlqSinkProperty: FlinkSinkProperties,
//                           serializer: IN => KafkaJsValueDto, // serializeToKafkaJsValue(val)  == val.serializeToKafkaJsValue
                           producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto],
                           serializeFun: OutDtoWithErrors[IN] => KafkaJsValueDto, // serializeToKafkaJsValue(val)
                       ): DataStreamSink[KafkaDto] = {
      implicit val typeInformationIn: TypeInformation[IN] = self.dataType

      val mf : (IN, Option[JsMaskedPath]) => Either[List[JsMaskedPathError], KafkaDto] ={(q,w )=>
        val dto = dlqSinkPropertyWithSerializer.serializerToKafkaJsValue(q)
         w
          .map(mp => dto.jsValue.toMaskedJson(mp))
          .getOrElse(Right(dto.jsValue))
          .map(s =>KafkaDto(dto.id.getBytes(), Json.stringify(s).getBytes()) )
      }

      FlinkKafkaFun.createProducerWithMetric[IN](
        self,
        serviceData,
        dlqSinkPropertyWithSerializer.flinkSinkProperties,
        mf,
        Some(value = dlqSinkPropertyWithSerializer.flinkSinkProperties -> { (q: OutDtoWithErrors[IN], w: Option[JsMaskedPath]) =>
          val dto = serializeFun(q.copy(data = None))
          Right(KafkaDto(dto.id.getBytes(), Json.stringify(dto.jsValue).getBytes()))
        }),
//        None,
        producerFactory
      )
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
    private [common] def maskedProducer(serviceData: ServiceDataDto,
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

    def registerConsumerWithMetric[O: TypeInformation](
                                                         serviceData: ServiceDataDto,
                                                         consumerProperties: FlinkConsumerProperties,
                                                         dlqProducer: Option[FlinkSinkProperties],
                                                         serialisationProcessFunction: AbstractDlqProcessFunction[Array[Byte], O, JsMaskedPathError],
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
