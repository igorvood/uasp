package ru.vtb.uasp.common.base

import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.scala.DataStream

trait OutSideSinkService[IN, OUT] {
  def outsideSink(stream: DataStream[IN]): DataStreamSink[OUT]
}
