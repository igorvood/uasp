package ru.vtb.uasp.common.dto.entity.utils

import ru.vtb.uasp.common.kafka.{FlinkConsumerProperties, FlinkSinkProperties}
import ru.vtb.uasp.common.service.dto.ServiceDataDto
import ru.vtb.uasp.common.utils.config.PropertyUtil.{createByClass, i, l, propertyVal, s}
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, ReadConfigErrors}
import ru.vtb.uasp.common.utils.json.JsonConverter.mapFields
import ru.vtb.uasp.pilot.model.vector.dao.kafka.KafkaDtoSerializationService
import ru.vtb.uasp.pilot.model.vector.service.JsonConverterService

import scala.collection.mutable

case class ModelVectorPropsModel(
                                  maxParallelism: Int, //, = config.getOrElse(s"$sysPrefixName.max.parallelism", "8").toInt,

                                  appServiceName: ServiceDataDto, //= config.getOrElse (s"$appPrefixName.service.name", "model-vector-d")

                                  consumerTopicName: FlinkConsumerProperties, //= config.getOrElse (s"$appPrefixName.consumer.topic.name", "mock1")
                                  producerQaTopicName: FlinkSinkProperties, //= config.getOrElse (s"$appPrefixName.producer.qa.topic.name", "mock3")
                                  producerFSTopicName: FlinkSinkProperties, // = config.getOrElse (s"$appPrefixName.producer.fs.topic.name", "mock4")
                                  producerPosTopicName: FlinkSinkProperties, //= config.getOrElse (s"$appPrefixName.producer.pos.topic.name", "mock5")
                                  producerPosNewTopicName: FlinkSinkProperties, // = config.getOrElse (s"$appPrefixName.producer.posNew.topic.name", "mock15")
                                  producerPensTopicName: FlinkSinkProperties, //= config.getOrElse (s"$appPrefixName.producer.pens.topic.name", "mock6")
                                  producerNSTopicName: FlinkSinkProperties, //= config.getOrElse (s"$appPrefixName.producer.ns.topic.name", "mock7")

                                  producerCase8TopicName: FlinkSinkProperties, //= config.getOrElse (s"$appPrefixName.producer.case8.topic.name", "mock8")
                                  producerCase29TopicName: FlinkSinkProperties, //= config.getOrElse (s"$appPrefixName.producer.case29.topic.name", "mock9")
                                  producerCase44TopicName: FlinkSinkProperties, // = config.getOrElse (s"$appPrefixName.producer.case44.topic.name", "mock10")
                                  producerCase71TopicName: FlinkSinkProperties, // = config.getOrElse (s"$appPrefixName.producer.case71.topic.name", "mock11")
                                  producerCase51TopicName: FlinkSinkProperties, //= config.getOrElse (s"$appPrefixName.producer.case51.topic.name", "mock12")
                                  producerCase48TopicName: FlinkSinkProperties, //= config.getOrElse (s"$appPrefixName.producer.case48.topic.name", "mock13")
                                  producerCase68TopicName: FlinkSinkProperties, // = config.getOrElse (s"$appPrefixName.producer.case69.topic.name", "mock14")

                                  appStreamCheckpointTimeSeconds: Long, //= config.getOrElse (s"$sysPrefixName.stream.checkpoint.time.seconds", "120000").toLong
                                  appStateCheckpointsNumRetained: Int, // = config.getOrElse (s"$sysPrefixName.state.checkpoints.num-retained", "4").toInt

                                  enableQaStream: Boolean, //= config.getOrElse (s"$appPrefixName.enable.qa.stream", "false").toBoolean
                                  prefixQaStream: String, // = config.getOrElse (s"$appPrefixName.prefix.qa.stream", "test")
                                  transactionalId: String, //= config.getOrElse (s"$sysPrefixName.transactional.id", uidPrefix + "_transactionId")
                                ) {
  val kafkaDtoSerializationService = new KafkaDtoSerializationService()

  val flatJsonConverter = new JsonConverterService(mapFields)

}


object ModelVectorPropsModel extends ConfigurationInitialise[ModelVectorPropsModel] {

  val appPrefixDefaultName: String = "uasp-streaming-model-vector"

  override def defaultConfiguration(prf: String)(implicit allProps: AllApplicationProperties, readKey: mutable.Set[String]): ModelVectorPropsModel =
    ModelVectorPropsModel(prf)(allProps, ModelVectorPropsModel)

  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, ModelVectorPropsModel] =
    for {
      maxParallelism <- propertyVal[Int](s"$prf", "mv.max.parallelism")
      appServiceName <- ServiceDataDto.create(s"$prf.service")
      consumerTopicName <- FlinkConsumerProperties.create(s"$prf.consumer")

      producerQaTopicName <- createByClass(s"$prf.producer.qa", FlinkSinkProperties.getClass, { p => FlinkSinkProperties.create(p) })
      producerFSTopicName <- createByClass(s"$prf.producer.fs", FlinkSinkProperties.getClass, { p => FlinkSinkProperties.create(p) })
      producerPosTopicName <- createByClass(s"$prf.producer.pos", FlinkSinkProperties.getClass, { p => FlinkSinkProperties.create(p) })
      producerPosNewTopicName <- createByClass(s"$prf.producer.posNew", FlinkSinkProperties.getClass, { p => FlinkSinkProperties.create(p) })
      producerPensTopicName <- createByClass(s"$prf.producer.pens", FlinkSinkProperties.getClass, { p => FlinkSinkProperties.create(p) })
      producerNSTopicName <- createByClass(s"$prf.producer.ns", FlinkSinkProperties.getClass, { p => FlinkSinkProperties.create(p) })
      producerCase8TopicName <- createByClass(s"$prf.producer.case8", FlinkSinkProperties.getClass, { p => FlinkSinkProperties.create(p) })
      producerCase29TopicName <- createByClass(s"$prf.producer.case29", FlinkSinkProperties.getClass, { p => FlinkSinkProperties.create(p) })
      producerCase44TopicName <- createByClass(s"$prf.producer.case44", FlinkSinkProperties.getClass, { p => FlinkSinkProperties.create(p) })
      producerCase71TopicName <- createByClass(s"$prf.producer.case71", FlinkSinkProperties.getClass, { p => FlinkSinkProperties.create(p) })
      producerCase51TopicName <- createByClass(s"$prf.producer.case51", FlinkSinkProperties.getClass, { p => FlinkSinkProperties.create(p) })
      producerCase48TopicName <- createByClass(s"$prf.producer.case48", FlinkSinkProperties.getClass, { p => FlinkSinkProperties.create(p) })
      producerCase68TopicName <- createByClass(s"$prf.producer.case69", FlinkSinkProperties.getClass, { p => FlinkSinkProperties.create(p) })

      appStreamCheckpointTimeSeconds <- propertyVal[Long](s"$prf", "mv.stream.checkpoint.time.seconds")
      appStateCheckpointsNumRetained <- propertyVal[Int](s"$prf", "mv.state.checkpoints.num-retained")

      enableQaStream <- propertyVal[Boolean](s"$prf", "mv.enable.qa.stream")
      prefixQaStream <- propertyVal[String](s"$prf", "mv.prefix.qa.stream")(appProps, configurationInitialise, s)
      transactionalId <- propertyVal[String](s"$prf", "transactional.id")(appProps, configurationInitialise, s)

    } yield new ModelVectorPropsModel(
      maxParallelism = maxParallelism,
      appServiceName = appServiceName,
      consumerTopicName = consumerTopicName,
      producerQaTopicName = producerQaTopicName,
      producerFSTopicName = producerFSTopicName,
      producerPosTopicName = producerPosTopicName,
      producerPosNewTopicName = producerPosNewTopicName,
      producerPensTopicName = producerPensTopicName,
      producerNSTopicName = producerNSTopicName,
      producerCase8TopicName = producerCase8TopicName,
      producerCase29TopicName = producerCase29TopicName,
      producerCase44TopicName = producerCase44TopicName,
      producerCase71TopicName = producerCase71TopicName,
      producerCase51TopicName = producerCase51TopicName,
      producerCase48TopicName = producerCase48TopicName,
      producerCase68TopicName = producerCase68TopicName,
      appStreamCheckpointTimeSeconds = appStreamCheckpointTimeSeconds,
      appStateCheckpointsNumRetained = appStateCheckpointsNumRetained,
      enableQaStream = enableQaStream,
      prefixQaStream = prefixQaStream,
      transactionalId = transactionalId
    )
}