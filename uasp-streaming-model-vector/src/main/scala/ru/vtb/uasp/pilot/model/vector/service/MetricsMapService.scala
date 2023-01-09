package ru.vtb.uasp.pilot.model.vector.service

import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration
import org.apache.flink.metrics.Counter
import play.api.libs.json.JsObject


class MetricsMapService extends RichMapFunction[JsObject,JsObject] {
  @transient private var counter: Counter = _

  override def open(parameters: Configuration): Unit = {
    counter = getRuntimeContext
      .getMetricGroup
      .counter("UaspStreamingModelVector")
  }

  override def map(value: JsObject): JsObject = {
    counter.inc()
    value
  }
}
