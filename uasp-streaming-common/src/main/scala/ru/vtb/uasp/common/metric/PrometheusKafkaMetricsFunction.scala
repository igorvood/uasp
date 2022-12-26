package ru.vtb.uasp.common.metric

import ru.vtb.uasp.common.metric.FlowDirection.FlowDirection

class PrometheusKafkaMetricsFunction[T](
                                         private val topicName: String,
                                         private val direction: FlowDirection,
                                       )
  extends PrometheusMetricsFunction[T](s"${topicName}_${direction.toString.toLowerCase()}")
