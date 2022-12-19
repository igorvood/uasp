package ru.vtb.uasp.common.kafka

import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer

import java.util.Properties


object ConsumerFactory {
  def getKafkaConsumer[T](topic: String, des: DeserializationSchema[T], properties: Properties): FlinkKafkaConsumer[T] = {
    val consumer = new FlinkKafkaConsumer[T](topic, des, properties.clone.asInstanceOf[Properties])
    consumer.setStartFromGroupOffsets()
    consumer
  }

}
