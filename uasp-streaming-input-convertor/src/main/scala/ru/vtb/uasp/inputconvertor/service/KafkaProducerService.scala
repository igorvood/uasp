package ru.vtb.uasp.inputconvertor.service

import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer.Semantic
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaProducer, KafkaSerializationSchema}

import java.util.Properties
import scala.util.Random

object KafkaProducerService {
  def getKafkaProducer[T](topicName: String, kafkaProps: Properties,
                          serializer: KafkaSerializationSchema[T],
                          transactionalID: String = "",
                          producerSemantic: Semantic = Semantic.EXACTLY_ONCE,
                          kafkaPoolSize: Int) : FlinkKafkaProducer [ T ] = {
    val localKafkaProps = kafkaProps.clone.asInstanceOf[Properties]
    localKafkaProps.setProperty("client.id", Random.nextInt(999).toString)
    if (producerSemantic == Semantic.EXACTLY_ONCE)  {
      localKafkaProps.setProperty("enable.idempotence","true")
      localKafkaProps.setProperty("max.in.flight.requests.per.connection","1")
      localKafkaProps.setProperty("retries","1")
      localKafkaProps.setProperty("acks","all")
      localKafkaProps.setProperty("isolation.level", "read_committed")
      localKafkaProps.setProperty("transactional.id", transactionalID + "-id-" + Random.nextInt(999999999).toString)
    }
    new FlinkKafkaProducer[T](
      topicName,
      serializer,
      localKafkaProps,
      producerSemantic,
      kafkaPoolSize
      )
  }
}
