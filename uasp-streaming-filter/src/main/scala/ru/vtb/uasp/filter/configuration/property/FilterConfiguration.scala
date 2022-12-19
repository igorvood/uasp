package ru.vtb.uasp.filter.configuration.property

import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import ru.vtb.uasp.common.kafka.{FlinkConsumerProperties, FlinkSinkProperties}
import ru.vtb.uasp.common.service.UaspDeserializationProcessFunction
import ru.vtb.uasp.common.utils.config.PropertyUtil._
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, ReadConfigErrors}
import ru.vtb.uasp.filter.service.FilterProcessFunction

import scala.collection.mutable

case class FilterConfiguration(
                                executionEnvironmentProperty: ExecutionFlinkEnvironmentProperty,
                                filterRule: FilterRule,
                                consumerPropperty: FlinkConsumerProperties,
                                flinkSinkPropertiesOk: FlinkSinkProperties,
                                flinkSinkPropertiesErr: Option[FlinkSinkProperties],
                              ) {

  lazy val kafkaSource: FlinkKafkaConsumer[Array[Byte]] = consumerPropperty.createConsumer()

  lazy val filterProcessFunction = new FilterProcessFunction(filterRule)

  val deserializationProcessFunction = new UaspDeserializationProcessFunction

}


object FilterConfiguration extends ConfigurationInitialise[FilterConfiguration] {

  val appPrefixDefaultName = "uasp-streaming-filter"

  override def defaultConfiguration(prf: String)(implicit allProps: AllApplicationProperties, readKey: mutable.Set[String]): FilterConfiguration = FilterConfiguration(appPrefixDefaultName)(allProps, FilterConfiguration)

  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, FilterConfiguration] =
    for {
      executionEnvironmentProperty <- ExecutionFlinkEnvironmentProperty.create(s"$prf.flink.job")
      filterRule <- FilterRule.create(s"$prf.filter")
      consumerTopicName <- FlinkConsumerProperties.create(s"$prf.kafka.consumer")
      flinkSinkPropertiesOk <- FlinkSinkProperties.create(s"$prf.kafka.producer.filterTag-success")
      flinkSinkPropertiesErr <- createByClassOption(s"$prf.kafka.producer.filterTag-error", FlinkSinkProperties.getClass, { p =>
        FlinkSinkProperties.create(p)
      })
    } yield new FilterConfiguration(
      executionEnvironmentProperty = executionEnvironmentProperty,
      filterRule = filterRule,
      consumerPropperty = consumerTopicName,
      flinkSinkPropertiesOk = flinkSinkPropertiesOk,
      flinkSinkPropertiesErr = flinkSinkPropertiesErr,
    )
}