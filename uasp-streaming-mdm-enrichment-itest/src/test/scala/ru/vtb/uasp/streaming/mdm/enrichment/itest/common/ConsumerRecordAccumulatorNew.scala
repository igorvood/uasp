package ru.vtb.uasp.streaming.mdm.enrichment.itest.common

import org.apache.kafka.clients.consumer.ConsumerRecord

import java.util.concurrent.ConcurrentHashMap

trait ConsumerRecordAccumulatorNew[KAFKA_KEY, KAFKA_VAL, K, V, Z, NEW_KEY] {
  val records: ConcurrentHashMap[NEW_KEY, V] =
    new ConcurrentHashMap[NEW_KEY, V]()

  val filterPredecate: (NEW_KEY, V) => Boolean

  val keyDecoder: KAFKA_KEY => K

  val valueDecoder: KAFKA_VAL => V

  val keyMapper: (K, V ) => NEW_KEY

  def addRecord(consumerRecord: ConsumerRecord[KAFKA_KEY, KAFKA_VAL]): Boolean = {
    val k = keyDecoder(consumerRecord.key())
    val v = valueDecoder(consumerRecord.value())
    val newKey = keyMapper(k, v)
    if (filterPredecate(newKey, v)) {
      records.put(newKey, v)
      true
    } else false
  }

  def getCount: Long = records.size()

  def get(key: NEW_KEY): V

  def getAll(key: NEW_KEY): Z
}
