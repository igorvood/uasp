package ru.vtb.uasp.filter.configuration.inft

import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer
import ru.vtb.uasp.common.utils.config.kafka.KafkaPrdProperty
import ru.vtb.uasp.filter.configuration.property.KafkaProducerPropertyMap

trait KafkaProducerService[T] {
  implicit def kafkaSource( kafkaPrdProperty: KafkaPrdProperty, prop: KafkaProducerPropertyMap): Map[String, SinkFunction[T]]
}
