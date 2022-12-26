package ru.vtb.uasp.common.metric

import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration
import org.apache.flink.metrics.{Meter, MeterView}

class PrometheusMetricsFunction[T](private val nameGrp: String,
                                  ) extends RichMapFunction[T, T] {

  @transient private var meter: Meter = _

  override def open(parameters: Configuration): Unit = {
    val metricGroup = getRuntimeContext.getMetricGroup
      .addGroup(nameGrp)
    val totalCounter = metricGroup.counter("total")
    meter = metricGroup.meter("rate", new MeterView(totalCounter))
  }

  override def map(value: T): T = {
    meter.markEvent()
    value
  }
}
