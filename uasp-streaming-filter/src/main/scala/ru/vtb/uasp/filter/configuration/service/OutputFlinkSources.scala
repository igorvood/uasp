package ru.vtb.uasp.filter.configuration.service

import org.apache.flink.streaming.api.functions.sink.SinkFunction
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.kafka.FlinkSinkProperties.producerFactoryDefault
import ru.vtb.uasp.common.service.dto.KafkaDto
import ru.vtb.uasp.common.utils.config.kafka.KafkaPrdProperty
import ru.vtb.uasp.filter.configuration.inft.KafkaProducerService
import ru.vtb.uasp.filter.configuration.property.KafkaProducerPropertyMap

case class OutputFlinkSources() extends KafkaProducerService[KafkaDto] {

  override implicit def kafkaSource(kafkaPrdProperty: KafkaPrdProperty, prop: KafkaProducerPropertyMap): Map[String, SinkFunction[KafkaDto]] = {
    prop.topicAlias.map { producer =>
      val (alias, topicName) = producer
      val flinkKafkaProducer: SinkFunction[KafkaDto] = FlinkSinkProperties(topicName, kafkaPrdProperty).createSinkFunction(producerFactoryDefault)

      (alias, flinkKafkaProducer)
    }
  }
}
