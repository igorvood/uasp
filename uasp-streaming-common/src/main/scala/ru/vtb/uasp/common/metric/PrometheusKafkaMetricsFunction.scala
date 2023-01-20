package ru.vtb.uasp.common.metric

import ru.vtb.uasp.common.metric.FlowDirection.FlowDirection
import ru.vtb.uasp.common.service.dto.ServiceDataDto

class PrometheusKafkaMetricsFunction[T](serviceData: ServiceDataDto,
                                        private val topicName: String,
                                        private val direction: FlowDirection,
                                       )
  extends PrometheusMetricsFunction[T](serviceData, s"${serviceData.serviceNameNoVersion}_${topicName}_${direction.toString.toLowerCase()}") {

  require(topicName != null, {
    "topicName is null"
  })
  require(direction != null, {
    "direction is null"
  })


}


