package ru.vtb.uasp.mutator.configuration.property

import ru.vtb.uasp.common.kafka.{FlinkConsumerProperties, FlinkSinkProperties}
import ru.vtb.uasp.common.service.UaspDeserializationProcessFunction
import ru.vtb.uasp.common.utils.config.PropertyUtil.createByClassOption
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, ReadConfigErrors}
import ru.vtb.uasp.filter.configuration.property.{ExecutionFlinkEnvironmentProperty, FilterConfiguration, FilterRule}
import ru.vtb.uasp.filter.service.FilterProcessFunction
import ru.vtb.uasp.mutator.service.BusinessRulesService

import scala.collection.mutable

case class MutationConfiguration(consumerPropperty: FlinkConsumerProperties,
                                 businessExecutionEnvironmentProperty: ExecutionFlinkEnvironmentProperty,
                                 filterRule: FilterRule,
                                 businessDroolsList: List[String],
                                 flinkSinkPropertiesOk: FlinkSinkProperties,
                                 flinkSinkPropertiesErr: Option[FlinkSinkProperties],
                                ) {


  lazy val newMutateService: BusinessRulesService = BusinessRulesService(businessDroolsList)

  val deserializationProcessFunction = new UaspDeserializationProcessFunction

//  val filterConfiguration = new FilterConfiguration(businessExecutionEnvironmentProperty, null, consumerPropperty, flinkSinkPropertiesOk, flinkSinkPropertiesErr)

  lazy val filterProcessFunction = new FilterProcessFunction(filterRule)

}

object MutationConfiguration extends ConfigurationInitialise[MutationConfiguration] {

  val appPrefixDefaultName = "uasp-streaming-mutator"

  override def defaultConfiguration(prf: String)(implicit allProps: AllApplicationProperties, readKey: mutable.Set[String]): MutationConfiguration = MutationConfiguration(appPrefixDefaultName)(allProps, MutationConfiguration)

  import ru.vtb.uasp.common.utils.config.PropertyUtil.{propertyVal, s}

  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, MutationConfiguration] = {
    for {
      consumerTopicName <- FlinkConsumerProperties.create(s"$prf.kafka.consumer")
      executionEnvironmentProperty <- ExecutionFlinkEnvironmentProperty.create(s"$prf.rulles.flink.job.checkpoint")
      filterRule <- FilterRule.create(s"$prf.rulles.filter")
      businessDroolsList <- propertyVal[String](s"$prf.rulles.drools", "list")(appProps, configurationInitialise, s).map(s => s.split(",").toList)
      flinkSinkPropertiesOk <- FlinkSinkProperties.create(s"$prf.kafka.producers.filterErrorTag-success")
      flinkSinkPropertiesErr <- createByClassOption(s"$prf.kafka.producers.filterErrorTag-error", FlinkSinkProperties.getClass, { p =>
        FlinkSinkProperties.create(p)
      })

    } yield new MutationConfiguration(
      consumerTopicName,
      executionEnvironmentProperty,
      filterRule,
      businessDroolsList,
      flinkSinkPropertiesOk,
      flinkSinkPropertiesErr
    )
  }


}
