package ru.vtb.uasp.streaming.mdm.enrichment.itest.service

import org.apache.kafka.clients.consumer.KafkaConsumer
import ru.vtb.uasp.streaming.mdm.enrichment.itest.common.{ConsumerRecordAccumulator, Finisheable, FooCounter}

import java.time.Duration
import java.util
import java.util.Properties
import scala.collection.JavaConverters.iterableAsScalaIterableConverter

case class MinMax(min: Long, max: Long)

@deprecated
class ConsumerService[A, B, K, V, Z](properties: Properties, topics: util.Collection[String],
                                     consumerRecordAccumulator: ConsumerRecordAccumulator[A, B, K, V, Z])
  extends Thread with Finisheable {
  val countMessages: FooCounter = new FooCounter(0)
  val flag: FooCounter = new FooCounter(0)
  @volatile
  var isEnd = false
  var kafkaConsumer: KafkaConsumer[A, B] = null

  @volatile
  def isFinished: Boolean = isEnd

  def finish(): Unit = {
    flag.inc()
  }

  def getCountMessages: Long = {
    countMessages.get()
  }

  def getUsers: Long = consumerRecordAccumulator.getCount

  def get(key: K): V = consumerRecordAccumulator.get(key)

  def getAll(key: K): Z = consumerRecordAccumulator.getAll(key)

  override def run(): Unit = {
    kafkaConsumer = new KafkaConsumer[A, B](properties)
    try {
      kafkaConsumer.subscribe(topics)
      println("subscribe: " + topics)
      var i: Long = 0
      while (flag.get() == 0) {
        val records = kafkaConsumer.poll(Duration.ofSeconds(0L))
        for (record <- records.asScala) {
          i = i + 1
          if (consumerRecordAccumulator.addRecord(record)) {
            countMessages.inc()
          }
        }
      }
    } finally {
      isEnd = true
      kafkaConsumer.close()
    }
  }
}
