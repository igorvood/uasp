package ru.vtb.uasp.common.factory

import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.scala.{DataStream, OutputTag}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer
import ru.vtb.uasp.common.base.{OutSideSinkService, SinkService}

object DataStreamSinkFactory {
  implicit class SinkFactory[T: TypeInformation](val dataStream: DataStream[T]) {
    def sink(mapFunc: RichMapFunction[T,T], producer: FlinkKafkaProducer[T]): DataStreamSink[T] =
      dataStream.map(mapFunc).addSink(producer)

    def outSideSink(outputTag: OutputTag[T], mapFunc: RichMapFunction[T,T], producer: FlinkKafkaProducer[T]): DataStreamSink[T] =
      dataStream.getSideOutput(outputTag).sink(mapFunc, producer)

    def sink[O: TypeInformation](sinkService: SinkService[T, O]) : DataStreamSink[O] = sinkService.sink(dataStream)

    def outSideSink[O: TypeInformation](sinkService: OutSideSinkService[T, O]) : DataStreamSink[O] = sinkService.outsideSink(dataStream)
  }
}