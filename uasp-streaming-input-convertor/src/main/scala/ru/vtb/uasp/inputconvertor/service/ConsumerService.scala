package ru.vtb.uasp.inputconvertor.service

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.{Logger, LoggerFactory}
import ru.vtb.uasp.inputconvertor.utils.kafka.{ConsumerRecordAccumulator, FooCounter}

import java.time.Duration
import java.util
import java.util.Properties
import scala.collection.JavaConverters.iterableAsScalaIterableConverter

case class MinMax(min: Long, max: Long)

class ConsumerService[A,B,K,V](properties: Properties, topics: util.Collection[String],
                               consumerRecordAccumulator: ConsumerRecordAccumulator[A,B,K,V], maxIteration: Long) extends Thread
{
  val logger: Logger = LoggerFactory.getLogger(getClass)
  val countMessages: FooCounter = new FooCounter
  var kafkaConsumer: KafkaConsumer[A, B] = null
  val flag: FooCounter = new FooCounter

  def finish(): Unit = {
    flag.inc()
  }

  def getCountMessages(): Long = {
    countMessages.get()
  }

  def getUsers: Long = consumerRecordAccumulator.getCount

  def get(key: K):V = consumerRecordAccumulator.get(key)

  override def run(): Unit = {
    kafkaConsumer = new KafkaConsumer[A, B](properties)
    try {
      kafkaConsumer.subscribe(topics)
      kafkaConsumer.poll(Duration.ofMillis(1000))
      kafkaConsumer.seekToBeginning(kafkaConsumer.assignment())
      logger.info("subscribe: " + topics)
      var i: Long = 0
      var iter : Long = 0
      while (flag.get() == 0 && iter != maxIteration) {
        iter += 1
        //logger.info(iter)
        val records = kafkaConsumer.poll(Duration.ofMillis(1000))
        for (record <- records.asScala) {
          i = i + 1
          consumerRecordAccumulator.addRecord(record)
          //logger.info(record)
          countMessages.inc()
          if (i > countMessages.get()) {
            logger.info("correction countMessages, i: " + i + ", countMessages: " + countMessages.get())
            countMessages.set(i)
          }
        }
      }
    } finally {
      kafkaConsumer.close()
    }
  }
}
