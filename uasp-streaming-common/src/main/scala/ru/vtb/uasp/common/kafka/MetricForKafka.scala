package ru.vtb.uasp.common.kafka

import ru.vtb.uasp.common.metric.{FlowDirection, PrometheusKafkaMetricsFunction}
import ru.vtb.uasp.common.service.dto.ServiceDataDto

trait MetricForKafka extends Serializable {

  def prometheusMetric[T](serviceData: ServiceDataDto): PrometheusKafkaMetricsFunction[T] = {
    this match {
      case FlinkConsumerProperties(fromTopic, _) => new PrometheusKafkaMetricsFunction[T](serviceData, fromTopic.replace("dev_", ""), FlowDirection.IN)
      case FlinkSinkProperties(toTopic, _, _, _, _) => new PrometheusKafkaMetricsFunction[T](serviceData, toTopic.replace("dev_", ""), FlowDirection.OUT)
    }
  }
}
