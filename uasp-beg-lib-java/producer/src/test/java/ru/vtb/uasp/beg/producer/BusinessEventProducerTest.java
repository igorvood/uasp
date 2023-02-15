package ru.vtb.uasp.beg.producer;

import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.SchemaRegistryClient;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.impl.SchemaRegistryClientBuilder;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportManagerClient;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportState;
import ru.vtb.uasp.beg.producer.utils.BadBusinessEvent;
import ru.vtb.uasp.beg.producer.utils.SimpleBusinessEvent1;
import ru.vtb.uasp.beg.producer.utils.SimpleBusinessEvent2;
import ru.vtb.uasp.beg.producer.utils.TestTransportManagerClient;

import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = BusinessEventProducerTest.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext
@EmbeddedKafka(partitions = 1, controlledShutdown = true, topics = {BusinessEventProducerTest.TEST_TOPIC})
class BusinessEventProducerTest {
    public static final String TEST_TOPIC = "test.topic";
    public static final String TEST_GROUP = "test.group";

    private BusinessEventProducer businessEventProducer;
    private TransportManagerClient transportManagerClient;
    private SchemaRegistryClient schemaRegistryClient;
    private KafkaConsumer<String, String> kafkaConsumer;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        transportManagerClient = spy(new TestTransportManagerClient(BusinessEventProducerTest::createTransportState));
        schemaRegistryClient = spy(SchemaRegistryClientBuilder.local()
                .schemaRegistryDirectory("classpath:schemaRegistry")
                .build());
        kafkaConsumer = new KafkaConsumer<>(getConsumerProps(embeddedKafka));
        embeddedKafka.consumeFromAnEmbeddedTopic(kafkaConsumer, TEST_TOPIC);
    }

    @Test
    @DisplayName("BusinessEventProducer разрешен и схема найдена")
    void sendTest() {
        businessEventProducer = new BusinessEventProducerBuilder()
                .kafkaProperties(getProducerProps(embeddedKafka))
                .transportStateManager(transportManagerClient)
                .schemaRegistryClient(schemaRegistryClient)
                .build();

        // Отправка событий SimpleBusinessEvent1 (key=null)
        int count1 = ThreadLocalRandom.current().nextInt(10, 100);
        Stream.generate(() -> ThreadLocalRandom.current().nextInt())
                .map(SimpleBusinessEvent1::new)
                .limit(count1)
                .forEach(businessEventProducer::send);
        // Отправка событий SimpleBusinessEvent2 (key=<id>)
        int count2 = ThreadLocalRandom.current().nextInt(10, 100);
        Stream.generate(() -> ThreadLocalRandom.current().nextInt())
                .map(i -> new SimpleBusinessEvent2(i % 10 + 1, i, null))
                .limit(count2)
                .forEach(e -> businessEventProducer.send(String.valueOf(e.getId()), e));
        // Прием событий из Kafka
        int countAll = count1 + count2;
        int countKeyed = 0;
        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(kafkaConsumer, 5000L, countAll);
        for (ConsumerRecord<String, String> record : records) {
            if (record.key() != null) countKeyed++;
        }
        assertEquals(countAll, records.count());
        assertEquals(count2, countKeyed);

        verify(transportManagerClient, times(count1)).getTransportState(SimpleBusinessEvent1.NAME);
        verify(transportManagerClient, times(count2)).getTransportState(SimpleBusinessEvent2.NAME);
        verify(schemaRegistryClient, times(count1)).getSchema(SimpleBusinessEvent1.NAME, SimpleBusinessEvent1.VERSION);
        verify(schemaRegistryClient, times(count2)).getSchema(SimpleBusinessEvent2.NAME, SimpleBusinessEvent2.VERSION);
    }

    @Test
    @DisplayName("BusinessEventProducer разрешен, но схема не найдена")
    void sendFailedTest() {
        businessEventProducer = new BusinessEventProducerBuilder()
                .kafkaProperties(getProducerProps(embeddedKafka))
                .transportStateManager(transportManagerClient)
                .schemaRegistryClient(schemaRegistryClient)
                .build();

        int count = ThreadLocalRandom.current().nextInt(1, 5);
        Stream.generate(() -> new BadBusinessEvent("data"))
                .limit(count)
                .forEach(businessEventProducer::send);
        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(kafkaConsumer, 1000L, 1);
        assertEquals(0, records.count());

        verify(transportManagerClient, times(count)).getTransportState("BadBusinessEvent");
        verify(schemaRegistryClient, times(count)).getSchema("BadBusinessEvent", "1");
    }

    private static Properties getProducerProps(EmbeddedKafkaBroker embeddedKafka) {
        Properties producerProps = new Properties();
        producerProps.putAll(KafkaTestUtils.producerProps(embeddedKafka.getBrokersAsString()));
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return producerProps;
    }

    private static Properties getConsumerProps(EmbeddedKafkaBroker embeddedKafka) {
        Properties consumerProps = new Properties();
        consumerProps.putAll(KafkaTestUtils.consumerProps(TEST_GROUP + "." + System.currentTimeMillis(), "true", embeddedKafka));
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return consumerProps;
    }

    private static TransportState createTransportState(String eventName) {
        TransportState result = new TransportState();
        result.setName(eventName);
        result.setTopicName(TEST_TOPIC);
        result.setEnabled(true);
        result.setKafkaAddress("kafka");
        result.setLastUpdateTimestamp(System.currentTimeMillis());
        return result;
    }

}