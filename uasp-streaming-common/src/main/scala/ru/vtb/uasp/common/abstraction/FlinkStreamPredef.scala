package ru.vtb.uasp.common.abstraction

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumerBase
import ru.vtb.uasp.common.kafka.{FlinkConsumerProperties, FlinkSinkProperties}
import ru.vtb.uasp.common.service.dto.KafkaDto

object FlinkStreamPredef {


  implicit class StreamFactory[T](val self: DataStream[T]) extends AnyVal {


    def processAndDlqSink[O: TypeInformation, DLQ: TypeInformation](process: DlqProcessFunction[T, O, DLQ], sinkDlqFunction: Option[SinkFunction[DLQ]]): DataStream[O] = {
      processAndDlqSink(process.getClass.getSimpleName, process, sinkDlqFunction)

    }


    def processAndDlqSink[O: TypeInformation, DLQ: TypeInformation](name: String, process: DlqProcessFunction[T, O, DLQ], sinkDlqFunction: Option[SinkFunction[DLQ]]): DataStream[O] = {
      val myBeDlq = self
        .process(process)
      sinkDlqFunction
        .map(sf => myBeDlq
          .getSideOutput(process.dlqOutPut)
          .addSink(sf)
          .name(name)
        )
      myBeDlq
    }

    def processAndDlqSinkWithMetric[O: TypeInformation](
                                                         process: DlqProcessFunction[T, O, KafkaDto],
                                                         sinkDlqFunction: Option[FlinkSinkProperties],
                                                         producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto]
                                                       ): DataStream[O] = {

      val myBeDlq = self
        .process(process)

      sinkDlqFunction
        .foreach(sf => {
          myBeDlq
            .getSideOutput(process.dlqOutPut)
            .map(sf.prometheusMetric[KafkaDto])
            .addSink(sf.createSinkFunction(producerFactory))
        }
        )
      myBeDlq
    }
  }

  implicit class StreamExecutionEnvironmentPredef(val self: StreamExecutionEnvironment) extends AnyVal {

    def registerConsumer[O: TypeInformation](name: String,
                                             consumer: FlinkKafkaConsumerBase[Array[Byte]],
                                             dlqProducer: SinkFunction[KafkaDto],
                                             serialisationProcessFunction: DlqProcessFunction[Array[Byte], O, KafkaDto]
                                            ): DataStream[O] = {
      registerConsumer(name, consumer, Some(dlqProducer), serialisationProcessFunction)
    }


    def registerConsumer[O: TypeInformation](name: String,
                                             consumer: FlinkKafkaConsumerBase[Array[Byte]],
                                             serialisationProcessFunction: DlqProcessFunction[Array[Byte], O, KafkaDto]
                                            ): DataStream[O] = {
      registerConsumer(name, consumer, None, serialisationProcessFunction)
    }


    def registerConsumer[O: TypeInformation](name: String,
                                             consumer: FlinkKafkaConsumerBase[Array[Byte]],
                                             dlqProducer: Option[SinkFunction[KafkaDto]],
                                             serialisationProcessFunction: DlqProcessFunction[Array[Byte], O, KafkaDto]
                                            ): DataStream[O] = {

      val producerImplicit: Option[SinkFunction[KafkaDto]] = dlqProducer
      self.addSource(consumer)
        .processAndDlqSink(name, serialisationProcessFunction, producerImplicit)
        .name(name)
    }

    def registerConsumerWithMetric[O: TypeInformation](
                                              consumerProperties: FlinkConsumerProperties,
                                              dlqProducer: Option[FlinkSinkProperties],
                                              serialisationProcessFunction: DlqProcessFunction[Array[Byte], O, KafkaDto],
                                              producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto]
                                            ): DataStream[O] = {
      val consumer = consumerProperties.createConsumer()

      self.addSource(consumer)
        .map(consumerProperties.prometheusMetric[Array[Byte]])
        .processAndDlqSinkWithMetric(serialisationProcessFunction, dlqProducer, producerFactory)
    }

  }


}
