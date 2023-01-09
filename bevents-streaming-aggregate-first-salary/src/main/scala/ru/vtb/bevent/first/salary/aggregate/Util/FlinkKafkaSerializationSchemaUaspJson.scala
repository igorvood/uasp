package ru.vtb.bevent.first.salary.aggregate.Util

import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.internals.RecordHeader
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.service.JsonConvertOutService

import java.lang
import scala.collection.JavaConverters.asJavaIterableConverter


class FlinkKafkaSerializationSchemaUaspJson(topic: String) extends KafkaSerializationSchema[UaspDto] {
  override def serialize(value: UaspDto, timestamp: lang.Long): ProducerRecord[Array[Byte], Array[Byte]] = {

    val headers = List[Header](new RecordHeader("schema", value.getClass.getSimpleName.getBytes))
    val dto = JsonConvertOutService.serializeToBytes(value)
    val producerRecord = new ProducerRecord(topic, null, dto.id,
      dto.value, headers.asJava)
    producerRecord
  }
}
