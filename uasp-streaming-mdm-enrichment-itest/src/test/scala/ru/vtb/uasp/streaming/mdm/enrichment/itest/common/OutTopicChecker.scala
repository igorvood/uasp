package ru.vtb.uasp.streaming.mdm.enrichment.itest.common

import ru.vtb.uasp.streaming.mdm.enrichment.itest.service.ConsumerServiceNew

import java.util.Properties
import scala.jdk.CollectionConverters.asJavaCollectionConverter

trait OutTopicChecker[A, B, K, V, Z, NEW_KEY] extends Finisheable {

  val consumerRecordAccumulator: ConsumerRecordAccumulatorNew[A, B, K, V, Z, NEW_KEY]

  val outTopicName: String

  val consumerKafkaProperties: Properties

  lazy val consumerService: ConsumerServiceNew[A, B, K, V, Z, NEW_KEY] = {
    val transformConsumerService = new ConsumerServiceNew(consumerKafkaProperties,
      Seq(outTopicName).asJavaCollection, consumerRecordAccumulator)
    transformConsumerService.start()
    Thread.sleep(5000)
    transformConsumerService
  }

  def isFinished: Boolean = consumerService.isFinished

}
