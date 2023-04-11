package ru.vtb.uasp.streaming.mdm.enrichment.itest.common

import org.apache.kafka.clients.consumer.ConsumerRecord

trait ConsumerRecordAccumulator[A, B, K, V, Z] {
  def addRecord(consumerRecord: ConsumerRecord[A, B]): Boolean

  def getCount: Long

  def get(key: K): V

  def getAll(key: K): Z
}
