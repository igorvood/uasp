package ru.vtb.uasp.common.kafka

import org.apache.flink.api.common.serialization.SerializationSchema
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaProducer, KafkaSerializationSchema}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer.Semantic

import java.util.Properties


object ProviderFactory {
  def getKafkaProvider[T](topic: String, des: SerializationSchema[T], properties: Properties): FlinkKafkaProducer[T] =
    new FlinkKafkaProducer[T](topic, des, properties)

  def getKafkaProvider[T](topic: String, ser: KafkaSerializationSchema[T], properties: Properties, kafkaPoolSize: Int): FlinkKafkaProducer[T] =
    new FlinkKafkaProducer[T](topic, ser, properties, Semantic.EXACTLY_ONCE, kafkaPoolSize)
}
