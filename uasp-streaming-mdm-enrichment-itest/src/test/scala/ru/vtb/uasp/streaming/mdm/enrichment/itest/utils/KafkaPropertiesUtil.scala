package ru.vtb.uasp.streaming.mdm.enrichment.itest.utils

import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import ru.vtb.uasp.streaming.mdm.enrichment.itest.entity.Config

import java.util.Properties

object KafkaPropertiesUtil {
  // val securityProtocol = "SSL"
  val securityProtocol = "PLAINTEXT"

  def getConsumerKafkaProperties(config: Config, groupId: String): Properties = {
    val properties = new Properties()
    properties.put("bootstrap.servers", config.bootstrapServers)
    properties.put("key.deserializer", classOf[ByteArrayDeserializer])
    properties.put("value.deserializer", classOf[ByteArrayDeserializer])
    properties.put("group.id", groupId)
    properties.put("enable.auto.commit", "true")
    properties.put("auto.offset.reset", "latest")
    properties.put("security.protocol", securityProtocol)
    properties.put("ssl.truststore.location", config.sslTruststoreLocation)
    properties.put("ssl.truststore.password", config.sslTruststorePassword)
    properties.put("ssl.keystore.location", config.sslKeystoreLocation)
    properties.put("ssl.keystore.password", config.sslKeystorePassword)
    properties.put("ssl.key.password", config.sslKeyPassword)
    properties.put("session.timeout.ms", "10000")

    properties
  }

  def getProducerKafkaProperties(config: Config): Map[String, String] = {
    Map(
      ProducerConfig.ACKS_CONFIG -> "1",
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> config.bootstrapServers,
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.ByteArraySerializer",
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.ByteArraySerializer",
      "security.protocol" -> securityProtocol, // "SSL",
      "ssl.truststore.location" -> config.sslTruststoreLocation,
      "ssl.truststore.password" -> config.sslTruststorePassword,
      "ssl.keystore.location" -> config.sslKeystoreLocation,
      "ssl.keystore.password" -> config.sslKeystorePassword,
      "ssl.key.password" -> config.sslKeyPassword
    )
  }
}
