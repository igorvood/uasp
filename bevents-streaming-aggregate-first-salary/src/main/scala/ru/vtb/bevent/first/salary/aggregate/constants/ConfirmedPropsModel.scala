package ru.vtb.bevent.first.salary.aggregate.constants

import ru.vtb.uasp.common.utils.config.ConfigUtils.getPropsFromMap

import java.util.Properties


case class ConfirmedPropsModel(config: Map[String, String]) {
  val appPrefixName: String = "aggregate-first-salary"
  val sysPrefixName: String = "aggregate-first-salary-sys"

  val appServiceName: String = config.getOrElse(s"$appPrefixName.service.name", "")

  val topicHA: String = config.getOrElse(s"$appPrefixName.ha.topic.name", "dev_bevents__realtime__enrichment_first_salary_transactions__uaspdto")
  val topicDlqName: String = config.getOrElse(s"$appPrefixName.dlq.topic.name", "dev_bevents__realtime__aggregate_first_salary__dlq")
  val topicOutputName: String = config.getOrElse(s"$appPrefixName.output.topic.name", "dev_bevents__realtime__aggregate_first_salary__uaspdto")

  val nameStateFirstSalaryAggregates: String = config.getOrElse(s"$appPrefixName.name.state.first.salary.aggregates", "bevent-streaming-aggregate-first-salary")

  val appStreamCheckpointTimeMilliseconds: Long = config.getOrElse(s"$sysPrefixName.stream.checkpoint.time.milliseconds", "10000").toLong
  val stateCheckpointsNumRetained: Int = config.getOrElse(s"$sysPrefixName.state.checkpoints.num-retained", "8").toInt
  val maxParallelism: Int = config.getOrElse(s"$sysPrefixName.max.parallelism", "8").toInt
  val streamCheckpointTimeoutMilliseconds: Long = config.getOrElse(s"$sysPrefixName.stream.checkpoint.timeout.milliseconds", "600000").toLong

  val listOfBusinessRuleLevel0: List[String] = config.getOrElse(s"$appPrefixName.list.of.business.rule.level0", "source_account.drl").split(",").toList
  val listOfBusinessRuleLevel1: List[String] = config.getOrElse(s"$appPrefixName.list.of.business.rule.level1", "level1.drl").split(",").toList
  val listOfBusinessRuleLevel2: List[String] = config.getOrElse(s"$appPrefixName.list.of.business.rule.level2", "level2.drl").split(",").toList
  val listOfBusinessRule: List[String] = config.getOrElse(s"$appPrefixName.list.of.business.rule", "first_sal.drl,first_pens.drl,first_ns.drl,first_pos.drl").split(",").toList


  val kafkaProducerPoolSize: Int = config.getOrElse(s"$appPrefixName.kafka.producer.pool.size", "5").toInt
  val commonKafkaProps: Properties = getPropsFromMap(
    config.filterKeys(key => key.startsWith(sysPrefixName))
      .map {
        case (k, v) => (k.replace(s"$sysPrefixName.", ""), v)
      }
  )

}
