package ru.vtb.uasp.common.dto.entity.utils

import ru.vtb.uasp.common.utils.config.ConfigUtils.getPropsFromMap

import java.util.Properties


case class ModelVectorPropsModel(config: Map[String, String], appPrefix: String = "") {
  val SERVICE_TYPE: String = "model-vector"
  val SERVICE_TYPE_SYS: String = "model-vector-sys"

  val instanceConfName: String = if (appPrefix.equals("")) config.getOrElse(SERVICE_TYPE + ".instance.conf.name", "") else appPrefix
  val appPrefixName: String = if (instanceConfName.isEmpty) SERVICE_TYPE else SERVICE_TYPE + "." + instanceConfName
  val sysPrefixName: String = SERVICE_TYPE_SYS + "." + instanceConfName

  val maxParallelism: Int = config.getOrElse(s"$sysPrefixName.max.parallelism", "8").toInt
  val appServiceName: String = config.getOrElse(s"$appPrefixName.service.name", "model-vector-d")

  val consumerTopicName: String = config.getOrElse(s"$appPrefixName.consumer.topic.name", "mock1")
  val producerTopicName: String = config.getOrElse(s"$appPrefixName.producer.topic.name", "mock2")
  val producerQaTopicName: String = config.getOrElse(s"$appPrefixName.producer.qa.topic.name", "mock3")
  val producerFSTopicName: String = config.getOrElse(s"$appPrefixName.producer.fs.topic.name", "mock4")
  val producerPosTopicName: String = config.getOrElse(s"$appPrefixName.producer.pos.topic.name", "mock5")
  val producerPosNewTopicName: String = config.getOrElse(s"$appPrefixName.producer.posNew.topic.name", "mock15")
  val producerPensTopicName: String = config.getOrElse(s"$appPrefixName.producer.pens.topic.name", "mock6")
  val producerNSTopicName: String = config.getOrElse(s"$appPrefixName.producer.ns.topic.name", "mock7")

  val producerCase8TopicName: String = config.getOrElse(s"$appPrefixName.producer.case8.topic.name", "mock8")
  val producerCase29TopicName: String = config.getOrElse(s"$appPrefixName.producer.case29.topic.name", "mock9")
  val producerCase44TopicName: String = config.getOrElse(s"$appPrefixName.producer.case44.topic.name", "mock10")
  val producerCase71TopicName: String = config.getOrElse(s"$appPrefixName.producer.case71.topic.name", "mock11")
  val producerCase51TopicName: String = config.getOrElse(s"$appPrefixName.producer.case51.topic.name", "mock12")
  val producerCase48TopicName: String = config.getOrElse(s"$appPrefixName.producer.case48.topic.name", "mock13")
  val producerCase68TopicName: String = config.getOrElse(s"$appPrefixName.producer.case69.topic.name", "mock14")

  val appStreamCheckpointTimeMilliseconds: String = config.getOrElse(s"$sysPrefixName.stream.checkpoint.time.milliseconds", "10000")
  val appStreamCheckpointTimeSeconds: Long = config.getOrElse(s"$sysPrefixName.stream.checkpoint.time.seconds", "120000").toLong
  val appStateCheckpointsNumRetained: Int = config.getOrElse(s"$sysPrefixName.state.checkpoints.num-retained", "4").toInt
  val kafkaProducerPoolSize: Int = config.getOrElse(s"$appPrefixName.kafka.producer.pool.size", "5").toInt

  val enableQaStream: Boolean = config.getOrElse(s"$appPrefixName.enable.qa.stream", "false").toBoolean
  val prefixQaStream: String = config.getOrElse(s"$appPrefixName.prefix.qa.stream", "test")
  val uidPrefix: String = config.getOrElse(s"$appPrefixName.uid-prefix", "model-vector")
  val transactionalId: String = config.getOrElse(s"$sysPrefixName.transactional.id", uidPrefix + "_transactionId")

  val commonKafkaProps: Properties = getPropsFromMap(
    config.filterKeys(key => key.startsWith(sysPrefixName))
      .map {
        case (k, v) => (k.replace(s"$sysPrefixName.", ""), v)
      }
  )

}
