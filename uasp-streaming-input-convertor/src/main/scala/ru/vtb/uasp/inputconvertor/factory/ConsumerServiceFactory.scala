package ru.vtb.uasp.inputconvertor.factory

import ru.vtb.uasp.inputconvertor.dao.StringAccumulator
import ru.vtb.uasp.inputconvertor.service.ConsumerService

import java.util.Properties
import scala.collection.JavaConverters.asJavaCollectionConverter

object ConsumerServiceFactory {
   def getAvroSchemaConsumerService(consumerKafkaProperties: Properties, topic:String, maxIteration: Long = Long.MaxValue):
   ConsumerService[Array[Byte], Array[Byte], String, String] = {
     val avroShchemaAccumulator: StringAccumulator = new StringAccumulator
     val avroSchemaConsumerService = new ConsumerService(consumerKafkaProperties, Seq(topic).asJavaCollection, avroShchemaAccumulator, maxIteration)
     avroSchemaConsumerService

   }
}
