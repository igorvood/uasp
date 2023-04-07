package ru.vtb.uasp.common.abstraction

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import play.api.libs.json.Json
import ru.vtb.uasp.common.kafka.{FlinkConsumerProperties, FlinkSinkProperties}
import ru.vtb.uasp.common.mask.MaskedPredef.MaskJsValuePredef
import ru.vtb.uasp.common.mask.dto.{JsMaskedPath, JsMaskedPathError}
import ru.vtb.uasp.common.service.JsonConvertOutService.JsonPredef
import ru.vtb.uasp.common.service.dto._

object FlinkStreamProducerPredef {

  implicit class StreamFactory[IN](val self: DataStream[IN]) extends AnyVal {
    def processWithMaskedDqlF[OUT: TypeInformation](serviceData: ServiceDataDto,
                                                    process: AbstractDlqProcessFunction[IN, OUT, OutDtoWithErrors[IN]],
                                                    sinkDlqPropertyWithSerializer: Option[PropertyWithSerializer[OutDtoWithErrors[IN]]],
                                                    producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto],
                                                   ): DataStream[OUT] = {
      implicit val typeInformationIn: TypeInformation[IN] = self.dataType
      FlinkKafkaFun.processAndDlqSinkWithMetric(self, serviceData, process, sinkDlqPropertyWithSerializer, producerFactory)
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

    def maskedProducerFErr(serviceData: ServiceDataDto,
                           dlqSinkPropertyWithSerializer: PropertyWithSerializer[IN],
                           producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto],
                           serializeFun: OutDtoWithErrors[IN] => KafkaJsValueDto, // serializeToKafkaJsValue(val)
                          ): DataStreamSink[KafkaDto] = {
      implicit val typeInformationIn: TypeInformation[IN] = self.dataType

      val mf: (IN, Option[JsMaskedPath]) => Either[List[JsMaskedPathError], KafkaDto] = { (q, w) =>
        val dto = dlqSinkPropertyWithSerializer.serializerToKafkaJsValue(q)
        w
          .map(mp => dto.jsValue.toMaskedJson(mp))
          .getOrElse(Right(dto.jsValue))
          .map(s => KafkaDto(dto.id.getBytes(), Json.stringify(s).getBytes()))
      }

      val maybeUnit: Option[PropertyWithSerializer[OutDtoWithErrors[IN]]] = dlqSinkPropertyWithSerializer.flinkSinkProperties.jsMaskedPath
        .map { mf => PropertyWithSerializer(dlqSinkPropertyWithSerializer.flinkSinkProperties, { in => serializeFun(in.copy(data = None)) }) }

      FlinkKafkaFun.createProducerWithMetric[IN](
        self,
        serviceData,
        dlqSinkPropertyWithSerializer,
        maybeUnit,
        producerFactory
      )
    }

    def maskedProducerF(serviceData: ServiceDataDto,
                        sinkPropertyWithSerializer: PropertyWithSerializer[IN],
                        sinkDlqPropertyWithSerializer: Option[PropertyWithSerializer[OutDtoWithErrors[IN]]],
                        producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto],
                       ): DataStreamSink[KafkaDto] = {
      implicit val typeInformationIn: TypeInformation[IN] = self.dataType
      FlinkKafkaFun.createProducerWithMetric(
        self,
        serviceData,
        sinkPropertyWithSerializer,
        sinkDlqPropertyWithSerializer,
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
        val errorStream: DataStream[JsMaskedPathError] = mainStream
          .getSideOutput(serialisationProcessFunction.dlqOutPut)

        errorStream.maskedProducerF(
          serviceData = serviceData,

          PropertyWithSerializer[JsMaskedPathError](dlq, { mpe =>
            mpe.serializeToKafkaJsValue
          }),
          Some(PropertyWithSerializer[OutDtoWithErrors[JsMaskedPathError]](
            dlq,
            { a: OutDtoWithErrors[JsMaskedPathError] =>
              val errs = a.data.map(m => m.error :: a.errors).getOrElse(a.errors)
              val value = a.copy[JsMaskedPathError](errors = errs, data = None)
              val value1 = value.serializeToKafkaJsValue
              value1
            }
          )
          ),
          producerFactory = producerFactory
        )

      }
      mainStream
    }
  }

}
