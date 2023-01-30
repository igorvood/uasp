package ru.vtb.uasp.inputconvertor.utils.serialization

import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema
import org.apache.kafka.clients.producer.ProducerRecord
import ru.vtb.uasp.inputconvertor.entity.CommonMessageType
import ru.vtb.uasp.inputconvertor.utils.json.JsonUtils.JsonToByteArray

import java.lang

class DLQSerializer(topic: String) extends KafkaSerializationSchema[CommonMessageType] {
  override def serialize(element: CommonMessageType, timestamp: lang.Long): ProducerRecord[Array[Byte], Array[Byte]] =
    new ProducerRecord(topic, Array[Byte](), JsonToByteArray(element))
}



