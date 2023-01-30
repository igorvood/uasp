package ru.vtb.uasp.inputconvertor.utils.serialization

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor.getForClass
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema
import org.apache.kafka.clients.consumer.ConsumerRecord
import ru.vtb.uasp.inputconvertor.constants.Config
import ru.vtb.uasp.inputconvertor.entity.InputSchemaType

import java.nio.charset.StandardCharsets

//@SerialVersionUID(1L)
class InputSchemaTypeDeserialization() extends KafkaDeserializationSchema[InputSchemaType] {
  override def isEndOfStream(nextElement: InputSchemaType): Boolean = false

  override def deserialize(record: ConsumerRecord[Array[Byte], Array[Byte]]): InputSchemaType = {
    InputSchemaType(new String(record.key(), Config.charset), new String(record.value(), StandardCharsets.UTF_8))
  }

  override def getProducedType: TypeInformation[InputSchemaType] = getForClass(classOf[InputSchemaType])

}
