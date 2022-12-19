package ru.vtb.uasp.common.kafka

import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.internals.RecordHeader
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.avro.AvroSerializeUtil

import java.lang
import scala.collection.JavaConverters.asJavaIterableConverter

class FlinkKafkaSerializationSchemaUasp(topic: String) extends KafkaSerializationSchema[UaspDto] {
  override def serialize(value: UaspDto, timestamp: lang.Long): ProducerRecord[Array[Byte], Array[Byte]] = {
    val (encoder, writer) = EncodeFactory.getEncode
    val headers = List[Header](new RecordHeader("schema", value.getClass.getSimpleName.getBytes))
    val producerRecord = new ProducerRecord(topic, null, value.id.getBytes,
        AvroSerializeUtil.encode(value, encoder, writer), headers.asJava)
    producerRecord
  }
}
