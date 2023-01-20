package ru.vtb.uasp.common.metric

import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration
import org.apache.flink.metrics.{Meter, MeterView}
import ru.vtb.uasp.common.service.dto.ServiceDataDto

class PrometheusMetricsFunction[T](private val serviceData: ServiceDataDto,
                                   private val nameGrp: String,
                                  ) extends RichMapFunction[T, T] {

  require(serviceData !=null,{"serviceData is null"})
  require(nameGrp !=null,{"nameGrp is null"})

  @transient private var meter: Meter = _

  private lazy val nameMetric = s"${serviceData.serviceNameNoVersion}_$nameGrp"

  override def open(parameters: Configuration): Unit = {
    val metricGroup = getRuntimeContext.getMetricGroup
      .addGroup(nameMetric)
    val totalCounter = metricGroup.counter("total")
    meter = metricGroup.meter("rate", new MeterView(totalCounter))
  }

  override def map(value: T): T = {
    meter.markEvent()
    value
  }
}
