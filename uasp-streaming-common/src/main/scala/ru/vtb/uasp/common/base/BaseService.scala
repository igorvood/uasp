package ru.vtb.uasp.common.base

import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

abstract class BaseService[IN, OUT](val env: StreamExecutionEnvironment, val args: Array[String]) extends Service[IN, OUT] {
  def onProcess(streams: Option[IN] = None): DataStream[OUT]
}
