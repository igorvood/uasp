package ru.vtb.uasp.common.kafka

import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.internals.RecordHeader
import ru.vtb.uasp.common.service.dto.KafkaDto

import java.lang
import scala.collection.JavaConverters.asJavaIterableConverter

class FlinkKafkaSerializationSchema(topic: String)
  extends KafkaSerializationSchema[KafkaDto] {

  override def serialize(value: KafkaDto, timestamp: lang.Long): ProducerRecord[Array[Byte], Array[Byte]] = {
    val headers = List[Header](new RecordHeader("schema", value.getClass.getSimpleName.getBytes))
    val producerRecord = new ProducerRecord(topic, null, value.id, value.value, headers.asJava)
    producerRecord
  }

}