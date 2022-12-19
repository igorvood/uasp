package ru.vtb.uasp.common.kafka

import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer.Semantic
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaProducer, KafkaSerializationSchema}

import java.util.Properties

object KafkaProducerService {

  private val transactionIdKey = "transactional.id"

  def getKafkaProducer[T](topicName: String, kafkaProps: Properties,
                          serializer: KafkaSerializationSchema[T],
                          producerSemantic: Semantic,
                          kafkaProducerPoolSize: Int
                         ): FlinkKafkaProducer[T] = {
    val localKafkaProps = kafkaProps.clone.asInstanceOf[Properties]
    val newTransId = java.util.UUID.randomUUID().toString
    localKafkaProps.setProperty(transactionIdKey, newTransId)

    new FlinkKafkaProducer[T](
      topicName,
      serializer,
      localKafkaProps,
      producerSemantic,
      kafkaProducerPoolSize
    )
  }
}
