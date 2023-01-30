package ru.vtb.uasp.inputconvertor.service

import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaConsumerBase, KafkaDeserializationSchema}

import java.util.Properties

object KafkaConsumerService {
  def getKafkaConsumer[T](topicName: String, kafkaProps: Properties,
                          deserializer: KafkaDeserializationSchema[T],
                          groupID: String,
                          startFromBeginning: Boolean = false): FlinkKafkaConsumerBase[T] = {
    val localKafkaProps = kafkaProps.clone.asInstanceOf[Properties]
    localKafkaProps.setProperty("group.id", groupID)

    val consumer = new FlinkKafkaConsumer[T](
      topicName,
      deserializer,
      localKafkaProps)
    if (startFromBeginning) consumer.setStartFromEarliest()
    else consumer.setStartFromGroupOffsets()
  }
}
