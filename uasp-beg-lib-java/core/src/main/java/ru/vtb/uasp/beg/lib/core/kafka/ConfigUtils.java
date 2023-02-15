package ru.vtb.uasp.beg.lib.core.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;
import java.util.Set;

public final class ConfigUtils {

    /**
     * Returns all possible parameters of Kafka Consumer from the specified properties starting from given prefix
     */
    public static Properties getKafkaConsumerProperties(Properties properties, String propertyPrefix) {
        return getKafkaProperties(properties, propertyPrefix + ".consumer.", ConsumerConfig.configNames());
    }

    /**
     * Returns all possible parameters of Kafka Consumer
     */
    public static Properties getKafkaConsumerProperties(Properties properties) {
        return getKafkaProperties(properties, ConsumerConfig.configNames());
    }

    /**
     * Returns all possible parameters of Kafka Producer from the specified properties starting from given prefix
     */
    public static Properties getKafkaProducerProperties(Properties properties, String propertyPrefix) {
        return getKafkaProperties(properties, propertyPrefix + ".producer.", ProducerConfig.configNames());
    }

    /**
     * Returns all possible parameters of Kafka Producer
     */
    public static Properties getKafkaProducerProperties(Properties properties) {
        return getKafkaProperties(properties, ProducerConfig.configNames());
    }

    /**
     * Returns all possible parameters of Kafka Producer/Consumer from the specified properties starting from given prefix
     *
     * @param properties         source properties
     * @param propertyPrefix     pprefix
     * @param possibleProperties ProducerConfig.configNames / ConsumerConfig.configNames
     * @return recognized properties
     */
    public static Properties getKafkaProperties(final Properties properties, String propertyPrefix, Set<String> possibleProperties) {
        Properties result = new Properties();
        if (!propertyPrefix.endsWith(".")) propertyPrefix = propertyPrefix + ".";
        for (String propName : properties.stringPropertyNames()) {
            if (!propName.startsWith(propertyPrefix)) continue;
            String possiblePropName = propName.substring(propertyPrefix.length());
            if (possibleProperties.contains(possiblePropName)) {
                result.put(possiblePropName, properties.getProperty(propName));
            }
        }
        return result;
    }

    /**
     * Returns all possible parameters of Kafka Producer/Consumer from the specified properties
     *
     * @param properties         source properties
     * @param possibleProperties ProducerConfig.configNames / ConsumerConfig.configNames
     * @return recognized properties
     */
    public static Properties getKafkaProperties(final Properties properties, Set<String> possibleProperties) {
        Properties result = new Properties();
        for (String propName : properties.stringPropertyNames()) {
            if (possibleProperties.contains(propName)) {
                result.put(propName, properties.getProperty(propName));
            }
        }
        return result;
    }
}
