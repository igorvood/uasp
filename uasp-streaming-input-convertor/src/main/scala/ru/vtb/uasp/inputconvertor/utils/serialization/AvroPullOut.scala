package ru.vtb.uasp.inputconvertor.utils.serialization

import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema
import org.apache.kafka.clients.producer.ProducerRecord
import ru.vtb.uasp.inputconvertor.constants.Config
import ru.vtb.uasp.inputconvertor.entity.CommonMessageType

import java.lang

class AvroPullOut(topic: String) extends KafkaSerializationSchema[CommonMessageType] {
  override def serialize(element: CommonMessageType, timestamp: lang.Long): ProducerRecord[Array[Byte], Array[Byte]] = {
    val msg = new ProducerRecord(topic, element.message_key.getBytes(Config.charset), element.avro_message.get)
    //val header = new RecordHeader("schemaKey", element.json_schemakey.get.getBytes(Config.charset))
    //msg.headers().add(header)
    msg
  }
}



