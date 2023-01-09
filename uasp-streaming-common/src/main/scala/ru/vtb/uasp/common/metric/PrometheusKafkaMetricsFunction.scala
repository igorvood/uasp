package ru.vtb.uasp.common.metric

import ru.vtb.uasp.common.metric.FlowDirection.FlowDirection
import ru.vtb.uasp.common.service.dto.ServiceDataDto

class PrometheusKafkaMetricsFunction[T](private val serviceData: ServiceDataDto,
                                        private val topicName: String,
                                        private val direction: FlowDirection,
                                       )
  extends PrometheusMetricsFunction[T](serviceData, s"${topicName}_${direction.toString.toLowerCase()}")
