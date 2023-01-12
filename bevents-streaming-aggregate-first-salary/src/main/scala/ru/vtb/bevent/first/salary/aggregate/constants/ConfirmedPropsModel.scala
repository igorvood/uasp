package ru.vtb.bevent.first.salary.aggregate.constants

import ru.vtb.bevent.first.salary.aggregate.factory.{BusinessRules, BusinessRulesFactory}
import ru.vtb.bevent.first.salary.aggregate.service.AggregateFirstSalaryRichMapFunction
import ru.vtb.uasp.common.kafka.{FlinkConsumerProperties, FlinkSinkProperties}
import ru.vtb.uasp.common.service.dto.ServiceDataDto
import ru.vtb.uasp.common.utils.config.PropertyUtil.{i, l, propertyVal, s}
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, ReadConfigErrors}

import scala.collection.mutable

case class ConfirmedPropsModel(
                                   appServiceName: ServiceDataDto, //= config.getOrElse(s"$appPrefixName.service.name", "")

                                   topicHA: FlinkConsumerProperties, // = config.getOrElse (s"$appPrefixName.ha.topic.name", "dev_bevents__realtime__enrichment_first_salary_transactions__uaspdto")
                                   topicDlqName: FlinkSinkProperties, // = config.getOrElse (s"$appPrefixName.dlq.topic.name", "dev_bevents__realtime__aggregate_first_salary__dlq")
                                   topicOutputName: FlinkSinkProperties, // = config.getOrElse (s"$appPrefixName.output.topic.name", "dev_bevents__realtime__aggregate_first_salary__uaspdto")

                                   nameStateFirstSalaryAggregates: String, // = config.getOrElse (s"$appPrefixName.name.state.first.salary.aggregates", "bevent-streaming-aggregate-first-salary")

                                   appStreamCheckpointTimeMilliseconds: Long, // = config.getOrElse (s"$sysPrefixName.stream.checkpoint.time.milliseconds", "10000").toLong
                                   stateCheckpointsNumRetained: Int, // = config.getOrElse (s"$sysPrefixName.state.checkpoints.num-retained", "8").toInt
                                   maxParallelism: Int, //= config.getOrElse (s"$sysPrefixName.max.parallelism", "8").toInt
                                   streamCheckpointTimeoutMilliseconds: Long, //= config.getOrElse (s"$sysPrefixName.stream.checkpoint.timeout.milliseconds", "600000").toLong

                                   listOfBusinessRuleLevel0: List[String], //= config.getOrElse (s"$appPrefixName.list.of.business.rule.level0", "source_account.drl").split (",").toList
                                   listOfBusinessRuleLevel1: List[String], // = config.getOrElse (s"$appPrefixName.list.of.business.rule.level1", "level1.drl").split (",").toList
                                   listOfBusinessRuleLevel2: List[String], // = config.getOrElse (s"$appPrefixName.list.of.business.rule.level2", "level2.drl").split (",").toList
                                   listOfBusinessRule: List[String], // = config.getOrElse (s"$appPrefixName.list.of.business.rule", "first_sal.drl,first_pens.drl,first_ns.drl,first_pos.drl").split (",").toList

                                 ) {

  lazy val businessRules: BusinessRules = BusinessRulesFactory.getBusinessRules(listOfBusinessRuleLevel0, listOfBusinessRuleLevel1, listOfBusinessRuleLevel2, listOfBusinessRule)

  lazy val aggregateFirstSalaryRichMapFunction = new AggregateFirstSalaryRichMapFunction(businessRules, nameStateFirstSalaryAggregates)
}

object ConfirmedPropsModel extends ConfigurationInitialise[ConfirmedPropsModel] {

  val appPrefixDefaultName = "bevents-streaming-aggregate-first-salary"

  override def defaultConfiguration(prf: String)(implicit allProps: AllApplicationProperties, readKey: mutable.Set[String]): ConfirmedPropsModel =
    ConfirmedPropsModel(appPrefixDefaultName)(allProps, ConfirmedPropsModel)

  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, ConfirmedPropsModel] =
    for {
      appServiceName <- ServiceDataDto.create(s"$prf.service") //= config.getOrElse(s"$appPrefixName.service.name", "")

      topicHA <- FlinkConsumerProperties.create(s"$prf.ha") // = config.getOrElse (s"$appPrefixName.ha.topic.name", "dev_bevents__realtime__enrichment_first_salary_transactions__uaspdto")
      topicDlqName <- FlinkSinkProperties.create(s"$prf.dlq") // = config.getOrElse (s"$appPrefixName.dlq.topic.name", "dev_bevents__realtime__aggregate_first_salary__dlq")
      topicOutputName <- FlinkSinkProperties.create(s"$prf.output") // = config.getOrElse (s"$appPrefixName.output.topic.name", "dev_bevents__realtime__aggregate_first_salary__uaspdto")

      nameStateFirstSalaryAggregates <-
        propertyVal[String](s"$prf", "name.state.first.salary.aggregates")(appProps, configurationInitialise, s)
      // = config.getOrElse (s"$appPrefixName.name.state.first.salary.aggregates", "bevent-streaming-aggregate-first-salary")

      appStreamCheckpointTimeMilliseconds <- propertyVal[Long](s"$prf", "stream.checkpoint.time.milliseconds")
      // = config.getOrElse (s"$sysPrefixName.stream.checkpoint.time.milliseconds", "10000").toLong
      stateCheckpointsNumRetained <- propertyVal[Int](s"$prf", "state.checkpoints.num-retained") // = config.getOrElse (s"$sysPrefixName.state.checkpoints.num-retained", "8").toInt
      maxParallelism <- propertyVal[Int](s"$prf", "max.parallelism") //= config.getOrElse (s"$sysPrefixName.max.parallelism", "8").toInt
      streamCheckpointTimeoutMilliseconds <- propertyVal[Long](s"$prf", "stream.checkpoint.timeout.milliseconds") //= config.getOrElse (s"$sysPrefixName.stream.checkpoint.timeout.milliseconds", "600000").toLong

      listOfBusinessRuleLevel0 <- propertyVal[String](s"$prf", "list.of.business.rule.level0")(appProps, configurationInitialise, s).map(s => s.split(",").toList)
      //= config.getOrElse (s"$appPrefixName.list.of.business.rule.level0", "source_account.drl").split (",").toList
      listOfBusinessRuleLevel1 <- propertyVal[String](s"$prf", "list.of.business.rule.level1")(appProps, configurationInitialise, s).map(s => s.split(",").toList)
      // = config.getOrElse (s"$appPrefixName.list.of.business.rule.level1", "level1.drl").split (",").toList
      listOfBusinessRuleLevel2 <- propertyVal[String](s"$prf", "list.of.business.rule.level2")(appProps, configurationInitialise, s).map(s => s.split(",").toList)
      // = config.getOrElse (s"$appPrefixName.list.of.business.rule.level2", "level2.drl").split (",").toList
      listOfBusinessRule <- propertyVal[String](s"$prf", "list.of.business.rule")(appProps, configurationInitialise, s).map(s => s.split(",").toList)
      // = config.getOrElse (s"$appPrefixName.list.of.business.rule", "first_sal.drl,first_pens.drl,first_ns.drl,first_pos.drl").split (",").toList

    } yield new ConfirmedPropsModel(
      appServiceName = appServiceName,
      topicHA = topicHA,
      topicDlqName = topicDlqName,
      topicOutputName = topicOutputName,
      nameStateFirstSalaryAggregates = nameStateFirstSalaryAggregates,
      appStreamCheckpointTimeMilliseconds = appStreamCheckpointTimeMilliseconds,
      stateCheckpointsNumRetained = stateCheckpointsNumRetained,
      maxParallelism = maxParallelism,
      streamCheckpointTimeoutMilliseconds = streamCheckpointTimeoutMilliseconds,
      listOfBusinessRuleLevel0 = listOfBusinessRuleLevel0,
      listOfBusinessRuleLevel1 = listOfBusinessRuleLevel1,
      listOfBusinessRuleLevel2 = listOfBusinessRuleLevel2,
      listOfBusinessRule = listOfBusinessRule
    )
}
