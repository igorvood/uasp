package ru.vtb.uasp.common.kafka

import ru.vtb.uasp.common.metric.{FlowDirection, PrometheusKafkaMetricsFunction}

trait MetricForKafka {

  def prometheusMetric[T] = {
    this match {
      case FlinkConsumerProperties(fromTopic, _) => new PrometheusKafkaMetricsFunction[T](fromTopic.replace("dev_", ""), FlowDirection.IN)
      case FlinkSinkProperties(toTopic, _, _, _) => new PrometheusKafkaMetricsFunction[T](toTopic.replace("dev_", ""), FlowDirection.OUT)
    }
  }
}
