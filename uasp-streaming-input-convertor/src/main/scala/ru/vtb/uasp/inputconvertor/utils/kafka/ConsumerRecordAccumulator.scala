package ru.vtb.uasp.inputconvertor.utils.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord

trait ConsumerRecordAccumulator[A, B, K, V] {
  def addRecord(consumerRecord: ConsumerRecord[A, B])

  def getCount: Long

  def get(key: K): V
}
