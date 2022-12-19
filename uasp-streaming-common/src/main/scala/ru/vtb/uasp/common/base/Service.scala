package ru.vtb.uasp.common.base

import org.apache.flink.streaming.api.scala.DataStream

trait Service[IN, OUT] {
  def onProcess(streams: Option[IN]): DataStream[OUT]
}
