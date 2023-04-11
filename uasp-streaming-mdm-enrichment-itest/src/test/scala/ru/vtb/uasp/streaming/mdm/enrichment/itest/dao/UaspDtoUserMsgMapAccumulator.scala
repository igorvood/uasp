package ru.vtb.uasp.streaming.mdm.enrichment.itest.dao

import com.sksamuel.avro4s.Decoder
import org.apache.avro.generic.{GenericDatumReader, GenericRecord}
import org.apache.kafka.clients.consumer.ConsumerRecord
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.avro.AvroDeserializeUtil
import ru.vtb.uasp.streaming.mdm.enrichment.itest.common.ConsumerRecordAccumulator
import ru.vtb.uasp.streaming.mdm.enrichment.itest.utils.IdsListGenerator.prefix

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap

class UaspDtoUserMsgMapAccumulator extends ConsumerRecordAccumulator[Array[Byte], Array[Byte], String,
  UaspDto, Map[String, UaspDto]] {
  val records: ConcurrentHashMap[String, Map[String, UaspDto]] =
    new ConcurrentHashMap[String, Map[String, UaspDto]]()

  override def addRecord(consumerRecord: ConsumerRecord[Array[Byte], Array[Byte]]): Boolean = {
    var result = false
    val key = new String(consumerRecord.key(), StandardCharsets.UTF_8)
    val decoder = Decoder[UaspDto]
    val reader = new GenericDatumReader[GenericRecord](decoder.schema)
    val decodeAvro = AvroDeserializeUtil.decode(ByteBuffer.wrap(consumerRecord.value()), decoder, reader)
    val userMessages = records.get(key)
    val idMsg = decodeAvro.dataString("idMsg")
    if (key.startsWith(prefix)) {
      if (userMessages == null) {
        records.put(key, Map(idMsg -> decodeAvro))
      } else {
        records.put(key, userMessages + (idMsg -> decodeAvro))
      }
      result = true
    }

    result
  }

  override def getCount: Long = records.size()

  override def get(key: String): UaspDto = records.get(key).last._2

  override def getAll(key: String): Map[String, UaspDto] = records.get(key)
}
