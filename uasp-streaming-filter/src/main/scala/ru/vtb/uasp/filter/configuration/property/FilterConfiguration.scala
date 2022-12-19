package ru.vtb.uasp.filter.configuration.property

import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import ru.vtb.uasp.common.kafka.FlinkConsumerProperties
import ru.vtb.uasp.common.service.UaspDeserializationProcessFunction
import ru.vtb.uasp.common.service.dto.KafkaDto
import ru.vtb.uasp.common.utils.config.PropertyUtil._
import ru.vtb.uasp.common.utils.config.kafka.{KafkaCnsProperty, KafkaPrdProperty}
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, ReadConfigErrors}
import ru.vtb.uasp.filter.configuration.service.OutputFlinkSources
import ru.vtb.uasp.filter.service.{FilterProcessFunction, KafkaSinksService, SinksService}

import scala.collection.mutable

case class FilterConfiguration(
                                kafkaProducerPropsMap: KafkaProducerPropertyMap,
                                executionEnvironmentProperty: ExecutionFlinkEnvironmentProperty,
                                filterRule: FilterRule,
                                kafkaCnsProperty: KafkaCnsProperty,
                                consumerTopicName: String,
                                kafkaPrdProperty: KafkaPrdProperty,
                              ) {

  lazy val kafkaSource: FlinkKafkaConsumer[Array[Byte]] = FlinkConsumerProperties(consumerTopicName, kafkaCnsProperty).createConsumer()

  lazy val producersMap: Map[String, SinkFunction[KafkaDto]] = OutputFlinkSources().kafkaSource(kafkaPrdProperty, kafkaProducerPropsMap)

  lazy val filterProcessFunction = new FilterProcessFunction(filterRule)

  val deserializationProcessFunction = new UaspDeserializationProcessFunction

  lazy val sinkService: KafkaSinksService = KafkaSinksService(filterRule, executionEnvironmentProperty, producersMap)

}


object FilterConfiguration extends ConfigurationInitialise[FilterConfiguration] {

  val appPrefixDefaultName = "filter"


//  override def create[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, FilterConfiguration] =
//    for {
//      kafkaProducerPropsMap <- KafkaProducerPropertyMap.create(s"$prf.app.kafka.producers") //(s"$appPrefixDefaultName.app.kafka.producers")
//      executionEnvironmentProperty <- ExecutionFlinkEnvironmentProperty.create(s"$prf.app.flink.job.checkpoint")
//      filterRule <- FilterRule.create(s"$prf.app.filter")
//      kafkaCnsProperty <- KafkaCnsProperty.create(s"$prf.app.kafka.consumer.property")
//      consumerTopicName <- propertyVal[String](s"$prf.app.kafka.consumer", "topicName")
//      kafkaPrdProperty <- KafkaPrdProperty.create(s"$prf.kafka")
//    } yield new FilterConfiguration(
//      kafkaProducerPropsMap = kafkaProducerPropsMap,
//      executionEnvironmentProperty = executionEnvironmentProperty,
//      filterRule = filterRule,
//      kafkaCnsProperty = kafkaCnsProperty,
//      consumerTopicName = consumerTopicName,
//      kafkaPrdProperty = kafkaPrdProperty
//    )
//
//  override def defaultConfiguration(prf: String)(implicit allProps: AllApplicationProperties, readKey: mutable.Set[String]): FilterConfiguration = FilterConfiguration(appPrefixDefaultName)(allProps, FilterConfiguration)

  override def defaultConfiguration(prf: String)(implicit allProps: AllApplicationProperties, readKey: mutable.Set[String]): FilterConfiguration = FilterConfiguration(appPrefixDefaultName)(allProps, FilterConfiguration)

  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, FilterConfiguration] =
    for {
            kafkaProducerPropsMap <- KafkaProducerPropertyMap.create(s"$prf.app.kafka.producers") //(s"$appPrefixDefaultName.app.kafka.producers")
            executionEnvironmentProperty <- ExecutionFlinkEnvironmentProperty.create(s"$prf.app.flink.job.checkpoint")
            filterRule <- FilterRule.create(s"$prf.app.filter")
            kafkaCnsProperty <- KafkaCnsProperty.create(s"$prf.app.kafka.consumer.property")
            consumerTopicName <- propertyVal[String](s"$prf.app.kafka.consumer", "topicName")(appProps,configurationInitialise, s)
            kafkaPrdProperty <- KafkaPrdProperty.create(s"$prf.kafka")
          } yield new FilterConfiguration(
            kafkaProducerPropsMap = kafkaProducerPropsMap,
            executionEnvironmentProperty = executionEnvironmentProperty,
            filterRule = filterRule,
            kafkaCnsProperty = kafkaCnsProperty,
            consumerTopicName = consumerTopicName,
            kafkaPrdProperty = kafkaPrdProperty
          )
}