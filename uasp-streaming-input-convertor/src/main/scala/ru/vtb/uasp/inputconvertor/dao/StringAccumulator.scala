package ru.vtb.uasp.inputconvertor.dao

import org.apache.kafka.clients.consumer.ConsumerRecord
import ru.vtb.uasp.inputconvertor.utils.kafka.ConsumerRecordAccumulator

import java.util.concurrent.ConcurrentHashMap

class StringAccumulator extends ConsumerRecordAccumulator[Array[Byte], Array[Byte], String, String] {
  val records: ConcurrentHashMap[String, String] = new ConcurrentHashMap[String, String]()

  override def addRecord(consumerRecord: ConsumerRecord[Array[Byte], Array[Byte]]): Unit = {
    //val key = new String(consumerRecord.key(), Config.charset)
    //val value = new String(consumerRecord.value(), Config.charset)

    records.put(consumerRecord.key.toString, consumerRecord.value.toString)
  }

  override def getCount: Long = records.size()

  override def get(key: String): String = records.get(key)
}
