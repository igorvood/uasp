package ru.vtb.ie.config

import org.apache.kafka.clients.producer.ProducerConfig

object KafkaPropertiesUtil {

  def getProducerKafkaProperties(config: Config): Map[String, String] = {
    Map(
      ProducerConfig.ACKS_CONFIG -> "1",
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG -> config.bootstrapServers,
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.ByteArraySerializer",
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.ByteArraySerializer",
      "security.protocol" -> "SSL",
      "ssl.truststore.location" -> config.sslTruststoreLocation,
      "ssl.truststore.password" -> config.sslTruststorePassword,
      "ssl.keystore.location" -> config.sslKeystoreLocation,
      "ssl.keystore.password" -> config.sslKeystorePassword,
      "ssl.key.password" -> config.sslKeyPassword
    )
  }
}
