package ru.vtb.uasp.mutator.configuration.property

import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import ru.vtb.uasp.common.kafka.FlinkConsumerProperties
import ru.vtb.uasp.common.service.JsonConvertInService
import ru.vtb.uasp.common.utils.config.PropertyUtil._
import ru.vtb.uasp.common.utils.config.kafka.{KafkaCnsProperty, KafkaPrdProperty}
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, ReadConfigErrors}
import ru.vtb.uasp.filter.configuration.property.{ExecutionFlinkEnvironmentProperty, FilterRule, KafkaProducerPropertyMap}
import ru.vtb.uasp.filter.service.{FilterProcessFunction, KafkaSinksService}
import ru.vtb.uasp.mutator.service.BusinessRulesService

import scala.collection.mutable

case class MutationConfiguration(consumerTopicName: String,
                                 kafkaCnsProperty: KafkaCnsProperty,
                                 businessExecutionEnvironmentProperty: ExecutionFlinkEnvironmentProperty,
                                 kafkaProducerPropsMap: KafkaProducerPropertyMap,
                                 businessDroolsList: List[String],
                                 filterRule: FilterRule,
                                 kafkaPrdProperty: KafkaPrdProperty,
                                ) {
  lazy val kafkaUaspDto: FlinkKafkaConsumer[Array[Byte]] = FlinkConsumerProperties(consumerTopicName, kafkaCnsProperty).createConsumer()

  lazy val convertInMapService = JsonConvertInService
  lazy val mutateService: BusinessRulesService = BusinessRulesService(businessDroolsList)

  lazy val filterProcessFunction = new FilterProcessFunction(filterRule)
  lazy val kafkaSinksService: KafkaSinksService = ru.vtb.uasp.filter.service.KafkaSinksService(filterRule, businessExecutionEnvironmentProperty, producersMap)

  private def producersMap = ru.vtb.uasp.filter.configuration.service.OutputFlinkSources().kafkaSource(kafkaPrdProperty, kafkaProducerPropsMap)
}

object MutationConfiguration extends ConfigurationInitialise[MutationConfiguration] {

  val appPrefixDefaultName = "bussines"

  override def defaultConfiguration(prf: String)(implicit allProps: AllApplicationProperties, readKey: mutable.Set[String]): MutationConfiguration = MutationConfiguration(appPrefixDefaultName)(allProps, MutationConfiguration)

  override def create[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, MutationConfiguration] = {
    for {


      consumerTopicName <- propertyVal[String](s"$prf.rulles.kafka.consumer", "topicName")
      kafkaCnsProperty <- KafkaCnsProperty.create(s"$prf.rulles.kafka.consumer.property")
      executionEnvironmentProperty <- ExecutionFlinkEnvironmentProperty.create(s"$prf.rulles.flink.job.checkpoint")
      kafkaProducerPropsMap <- KafkaProducerPropertyMap.create(s"$prf.rulles.kafka.producers")
      businessDroolsList <- propertyVal[String](s"$prf.rulles.drools", "list").map(s => s.split(",").toList)
      filterRule <- FilterRule.create(s"$prf.rulles.filter")
      kafkaPrdProperty <- KafkaPrdProperty.create(s"$prf.rulles.kafka.producer")

    } yield new MutationConfiguration(consumerTopicName, kafkaCnsProperty, executionEnvironmentProperty, kafkaProducerPropsMap, businessDroolsList, filterRule, kafkaPrdProperty)
  }

  private val prefixProducerProp = "bussines.rulles.kafka.producer"

  val bootstrapServers = "bootstrap.servers"
  val prefixConsumerProp = "bussines.rulles.kafka.consumer"


}
