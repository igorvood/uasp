package ru.vtb.uasp.inputconvertor.utils.serialization

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor.getForClass
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema
import org.apache.kafka.clients.consumer.ConsumerRecord
import ru.vtb.uasp.inputconvertor.constants.Config
import ru.vtb.uasp.inputconvertor.entity.InputMessageType


class InputMessageTypeDeserialization() extends KafkaDeserializationSchema[InputMessageType] {
  override def isEndOfStream(nextElement: InputMessageType): Boolean = false

  override def deserialize(record: ConsumerRecord[Array[Byte], Array[Byte]]): InputMessageType = {
    val outKey = if (record.key() != null) new String(record.key(), Config.charset) else ""
    InputMessageType(outKey, record.value())
  }

  override def getProducedType: TypeInformation[InputMessageType] = getForClass(classOf[InputMessageType])

}
