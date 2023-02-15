package ru.vtb.uasp.beg.lib.core.kafka;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

import static org.apache.kafka.common.security.auth.SecurityProtocol.SSL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigUtilsTest {

    private static final Properties testProperties = new Properties();

    @BeforeAll
    static void init() throws IOException {
        testProperties.load(ConfigUtilsTest.class.getClassLoader().getResourceAsStream("test-kafka.properties"));
    }

    @Test
    void getKafkaConsumerPropertiesTest() {
        // Получение параметров Kafka Consumer для топика с именем "topic1"
        Properties ps = ConfigUtils.getKafkaConsumerProperties(testProperties, "topic1");
        assertTopic1Consumer(ps);

        // Получение параметров Kafka Consumer для топика с именем "topic2"
        ps = ConfigUtils.getKafkaConsumerProperties(testProperties, "topic2");
        assertTopic2Consumer(ps);

        // Получение параметров Kafka Consumer для несуществующего топика
        ps = ConfigUtils.getKafkaConsumerProperties(testProperties, "unknown-topic");
        assertTrue(ps.isEmpty());
    }

    @Test
    void getKafkaProducerPropertiesTest() {
        // Получение параметров Kafka Producer для топика с именем "topic1"
        Properties ps = ConfigUtils.getKafkaProducerProperties(testProperties, "topic1");
        assertTopic1Producer(ps);

        // Получение параметров Kafka Producer для топика с именем "topic2"
        ps = ConfigUtils.getKafkaProducerProperties(testProperties, "topic2");
        assertTopic2Producer(ps);

        // Получение параметров Kafka Producer для несуществующего топика
        ps = ConfigUtils.getKafkaProducerProperties(testProperties, "unknown-topic");
        assertTrue(ps.isEmpty());
    }

    @Test
    void getKafkaPropertiesTest() {
        // Префикс без точки в конце
        Properties ps = ConfigUtils.getKafkaProperties(testProperties, "topic1.consumer", ConsumerConfig.configNames());
        assertTopic1Consumer(ps);
        ps = ConfigUtils.getKafkaProperties(testProperties, "topic1.producer", ProducerConfig.configNames());
        assertTopic1Producer(ps);

        // Префикс с точкой в конце
        ps = ConfigUtils.getKafkaProperties(testProperties, "topic2.consumer.", ConsumerConfig.configNames());
        assertTopic2Consumer(ps);
        ps = ConfigUtils.getKafkaProperties(testProperties, "topic2.producer.", ProducerConfig.configNames());
        assertTopic2Producer(ps);

        // Неправильный (несуществующий) префикс (нужно "topic1.consumer")
        ps = ConfigUtils.getKafkaProperties(testProperties, "topic1", ConsumerConfig.configNames());
        assertTrue(ps.isEmpty());

        // Неправильный список возможных параметров (ProducerConfig.configNames() для Consumer)
        ps = ConfigUtils.getKafkaProperties(testProperties, "topic1.consumer", ProducerConfig.configNames());
        assertEquals(1, ps.size());
        assertTrue(ps.containsKey(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG));

        // Пустой список возможных параметров
        ps = ConfigUtils.getKafkaProperties(testProperties, "topic1.consumer", Collections.emptySet());
        assertTrue(ps.isEmpty());
    }

    private static void assertTopic1Consumer(Properties ps) {
        assertEquals(5, ps.size());
        assertEquals(ps.getProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG), "server1:1111");
        assertEquals(ps.getProperty(ConsumerConfig.GROUP_ID_CONFIG), "group_1");
        assertEquals(ps.getProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG), "true");
        assertEquals(ps.getProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG), StringDeserializer.class.getName());
        assertEquals(ps.getProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG), StringDeserializer.class.getName());
    }

    private static void assertTopic1Producer(Properties ps) {
        assertEquals(3, ps.size());
        assertEquals(ps.getProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG), "server1:1111");
        assertEquals(ps.getProperty(ProducerConfig.BATCH_SIZE_CONFIG), "1000");
        assertEquals(ps.getProperty(ProducerConfig.RETRIES_CONFIG), "1");
    }

    private static void assertTopic2Consumer(Properties ps) {
        assertEquals(7, ps.size());
        assertEquals(ps.getProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG), "server2:2222");
        assertEquals(ps.getProperty(ConsumerConfig.GROUP_ID_CONFIG), "group_2");
        assertEquals(ps.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG), SSL.name());
        assertTrue(ps.containsKey(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG));
        assertTrue(ps.containsKey(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG));
        assertTrue(ps.containsKey(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG));
        assertTrue(ps.containsKey(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG));
    }

    private static void assertTopic2Producer(Properties ps) {
        assertEquals(9, ps.size());
        assertEquals(ps.getProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG), "server2:2222");
        assertEquals(ps.getProperty(ProducerConfig.RETRIES_CONFIG), "2");
        assertEquals(ps.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG), SSL.name());
        assertTrue(ps.containsKey(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG));
        assertTrue(ps.containsKey(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG));
        assertTrue(ps.containsKey(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG));
        assertTrue(ps.containsKey(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG));
        assertEquals(ps.getProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG), StringSerializer.class.getName());
        assertEquals(ps.getProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG), StringSerializer.class.getName());
    }

}