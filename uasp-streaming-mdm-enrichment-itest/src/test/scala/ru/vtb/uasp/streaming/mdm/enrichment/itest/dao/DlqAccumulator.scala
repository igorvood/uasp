package ru.vtb.uasp.streaming.mdm.enrichment.itest.dao

import org.apache.kafka.clients.consumer.ConsumerRecord
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.avro.AvroDeserializeUtil
import ru.vtb.uasp.streaming.mdm.enrichment.itest.common.ConsumerRecordAccumulator
import ru.vtb.uasp.streaming.mdm.enrichment.itest.scenario.CommonObject.{decoderUaspDto, genericDatumReaderUaspDto}
import ru.vtb.uasp.streaming.mdm.enrichment.itest.utils.IdsListGenerator.userIdLocal

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap

class DlqAccumulator extends ConsumerRecordAccumulator[Array[Byte], Array[Byte], String, UaspDto, Seq[UaspDto]] {
  val records: ConcurrentHashMap[String, UaspDto] =
    new ConcurrentHashMap[String, UaspDto]()

  override def addRecord(consumerRecord: ConsumerRecord[Array[Byte], Array[Byte]]): Boolean = {

    val value = new String(consumerRecord.value(), StandardCharsets.UTF_8)
    if (value.contains(userIdLocal)) {
      val decodeAvro = AvroDeserializeUtil.decode(ByteBuffer.wrap(consumerRecord.value()), decoderUaspDto, genericDatumReaderUaspDto)
      records.put(userIdLocal, decodeAvro)
      true
    } else false

  }

  override def getCount: Long = records.size()

  override def get(key: String): UaspDto = records.get(key)

  override def getAll(key: String): Seq[UaspDto] = Seq(records.get(key))

}