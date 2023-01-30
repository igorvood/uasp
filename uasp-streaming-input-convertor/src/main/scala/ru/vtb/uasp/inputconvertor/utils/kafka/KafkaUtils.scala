package ru.vtb.uasp.inputconvertor.utils.kafka

import ru.vtb.uasp.inputconvertor.factory.ConsumerServiceFactory

import java.util.{Properties, UUID}

object KafkaUtils {
  def getJsonSchema(topicName: String, kafkaProps: Properties, jsonSchemaKey: String, maxIteration: Int = 5): String = {
    val conusmerProps = kafkaProps.clone.asInstanceOf[Properties]
    conusmerProps.setProperty("auto.offset.reset", "earliest")
    conusmerProps.setProperty("group.id", UUID.randomUUID.toString)

    conusmerProps.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    conusmerProps.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")

    val avroSchemaConsumerService = ConsumerServiceFactory.getAvroSchemaConsumerService(conusmerProps,
      topicName, maxIteration)
    avroSchemaConsumerService.run()
    val staticJsonSchema = avroSchemaConsumerService.get(jsonSchemaKey)
    avroSchemaConsumerService.finish()
    if (staticJsonSchema == null) throw new RuntimeException("Null static Json schema for jsonSchemaKey: '"
      + jsonSchemaKey + "'")
    staticJsonSchema
  }

}
