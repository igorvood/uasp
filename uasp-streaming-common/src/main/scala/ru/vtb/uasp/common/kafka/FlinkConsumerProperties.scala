package ru.vtb.uasp.common.kafka

import org.apache.flink.api.common.serialization.AbstractDeserializationSchema
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import ru.vtb.uasp.common.kafka.FlinkConsumerProperties.deserializationSchema
import ru.vtb.uasp.common.utils.config.PropertyUtil.propertyVal
import ru.vtb.uasp.common.utils.config.kafka.KafkaCnsProperty
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, PropertyCombiner, ReadConfigErrors}

case class FlinkConsumerProperties(fromTopic: String,
                                   kafkaCnsProperty: KafkaCnsProperty,
                                  ) extends MetricForKafka {

  def createConsumer(): FlinkKafkaConsumer[Array[Byte]] = ConsumerFactory.getKafkaConsumer(
    fromTopic, deserializationSchema, kafkaCnsProperty.property)
}

object FlinkConsumerProperties extends PropertyCombiner[FlinkConsumerProperties] {
  val deserializationSchema: AbstractDeserializationSchema[Array[Byte]] = new AbstractDeserializationSchema[Array[Byte]]() {
    override def deserialize(bytes: Array[Byte]): Array[Byte] = bytes
  }

  protected override def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, FlinkConsumerProperties] =
    for {
      appTopicName <- propertyVal[String](prf, "fromTopic")
      kafkaCnsProperty <- KafkaCnsProperty.create(prf + ".fromTopic.cns")

    } yield new FlinkConsumerProperties(appTopicName, kafkaCnsProperty)
}
