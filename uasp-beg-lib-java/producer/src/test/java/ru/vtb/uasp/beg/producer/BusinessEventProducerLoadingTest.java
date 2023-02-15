package ru.vtb.uasp.beg.producer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.StopWatch;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.SchemaRegistryClient;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.impl.SchemaRegistryClientBuilder;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportManagerClient;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportState;
import ru.vtb.uasp.beg.producer.utils.ComplexBusinessEvent;
import ru.vtb.uasp.beg.producer.utils.SimpleBusinessEvent1;
import ru.vtb.uasp.beg.producer.utils.SimpleBusinessEvent2;
import ru.vtb.uasp.beg.producer.utils.TestTransportManagerClient;

import java.util.Properties;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.mockito.Mockito.spy;

/**
 * Нагрузочное тестирование BusinessEventProducer
 */
@SpringBootTest(classes = BusinessEventProducerLoadingTest.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@DirtiesContext
@EmbeddedKafka(partitions = 1, controlledShutdown = true, topics = {
        BusinessEventProducerLoadingTest.TEST_SIMPLE_TOPIC,
        BusinessEventProducerLoadingTest.TEST_COMPLEX_TOPIC
})
@Slf4j
class BusinessEventProducerLoadingTest {
    public static final String TEST_SIMPLE_TOPIC = "test__simple_topic";
    public static final String TEST_COMPLEX_TOPIC = "test__complex_topic";
    public static final String TEST_GROUP = "test.group";
    public static final String EXTERNAL_BROKER_ADDRESS = "localhost:29092";

    private BusinessEventProducer businessEventProducer;
    private TransportManagerClient transportManagerClient;
    private SchemaRegistryClient schemaRegistryClient;
    private KafkaConsumer<String, String> kafkaConsumer;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Disabled
    @Test
    @SneakyThrows
    void performSimpleEventsTest() {
        TestKafka kafka = new EmbeddedKafka(embeddedKafka);
        Function<String, TransportState> transportStateResolver = createTransportStateResolver(TEST_SIMPLE_TOPIC, kafka.getBrokerAddress());
        transportManagerClient = spy(new TestTransportManagerClient(transportStateResolver));
        schemaRegistryClient = spy(SchemaRegistryClientBuilder.local()
                .schemaRegistryDirectory("classpath:schemaRegistry")
                .build());
        businessEventProducer = new BusinessEventProducerBuilder()
                .kafkaProperties(kafka.getProducerProps())
                .transportStateManager(transportManagerClient)
                .schemaRegistryClient(schemaRegistryClient)
                .build();
        kafkaConsumer = new KafkaConsumer<>(kafka.getConsumerProps());
        embeddedKafka.consumeFromAnEmbeddedTopic(kafkaConsumer, TEST_SIMPLE_TOPIC);

        StopWatch sw = new StopWatch();
        int eventCount = 100000;
        int receivedCount = 0;

        log.info("Генерация и отправка {} событий SimpleBusinessEvent1 в очередь...", eventCount);
        sw.start("SimpleBusinessEvent1_Send");
        IntStream.range(1, eventCount + 1)
                .mapToObj(SimpleBusinessEvent1::new)
                .forEach(businessEventProducer::send);
        sw.stop();
        log.info("{} событий SimpleBusinessEvent1 отправлены за {} мс", eventCount, sw.getLastTaskTimeMillis());

        log.info("Генерация и отправка {} событий SimpleBusinessEvent2 в очередь...", eventCount);
        sw.start("SimpleBusinessEvent2_Send");
        IntStream.range(1, eventCount + 1)
                .mapToObj(i -> new SimpleBusinessEvent2(i % 10 + 1, i, "SimpleBusinessEvent #" + i))
                .forEach(e -> businessEventProducer.send(String.valueOf(e.getId()), e));
        sw.stop();
        log.info("{} событий SimpleBusinessEvent2 отправлены за {} мс", eventCount, sw.getLastTaskTimeMillis());

        log.info("Получение событий из Kafka...");
        sw.start("ConsumeEvents");
        while (receivedCount < eventCount * 2) {
            ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(kafkaConsumer, 1500L, 1);
            receivedCount += records.count();
            if (records.count() == 0) break;
        }
        sw.stop();
        log.info("Получение {} событий из Kafka выполнено за {} мс", receivedCount, sw.getLastTaskTimeMillis());
    }

    @Disabled
    @Test
    @SneakyThrows
    void performComplexEventTest() {
        TestKafka kafka = new ExternalKafka(EXTERNAL_BROKER_ADDRESS);
        Function<String, TransportState> transportStateResolver = createTransportStateResolver(TEST_COMPLEX_TOPIC, kafka.getBrokerAddress());
        transportManagerClient = spy(new TestTransportManagerClient(transportStateResolver));
        schemaRegistryClient = spy(SchemaRegistryClientBuilder.local()
                .schemaRegistryDirectory("classpath:schemaRegistry")
                .build());
        businessEventProducer = new BusinessEventProducerBuilder()
                .kafkaProperties(kafka.getProducerProps())
                .transportStateManager(transportManagerClient)
                .schemaRegistryClient(schemaRegistryClient)
                .build();

        StopWatch sw = new StopWatch();
        int eventCount = 100000;
        int receivedCount = 0;

        log.info("Генерация и отправка {} событий ComplexBusinessEvent в очередь...", eventCount);
        sw.start("ComplexBusinessEvent_Send");
        IntStream.range(1, eventCount + 1)
                .mapToObj(BusinessEventProducerLoadingTest::createComplexBusinessEvent)
                .forEach(e -> businessEventProducer.send(resolveComplexBusinessEventId(e), e));
        sw.stop();
        log.info("{} событий ComplexBusinessEvent отправлены за {} мс", eventCount, sw.getLastTaskTimeMillis());

        log.info("Получение событий из Kafka...");
        kafkaConsumer = new KafkaConsumer<>(kafka.getConsumerProps());
        embeddedKafka.consumeFromAnEmbeddedTopic(kafkaConsumer, TEST_COMPLEX_TOPIC);
        sw.start("ConsumeEvents");
        while (receivedCount < eventCount) {
            ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(kafkaConsumer, 1500L, 1);
            receivedCount += records.count();
            if (records.count() == 0) break;
        }
        sw.stop();
        log.info("Получение {} событий из Kafka выполнено за {} мс", receivedCount, sw.getLastTaskTimeMillis());
    }

    private static Function<String, TransportState> createTransportStateResolver(String topic, String brokers) {
        return (eventName) -> {
            TransportState result = new TransportState();
            result.setName(eventName);
            result.setTopicName(topic);
            result.setEnabled(true);
            result.setKafkaAddress(brokers);
            result.setLastUpdateTimestamp(System.currentTimeMillis());
            return result;
        };
    }

    private static String resolveComplexBusinessEventId(ComplexBusinessEvent event) {
        return String.valueOf(event.getByteField1());
    }

    private static ComplexBusinessEvent createComplexBusinessEvent(int i) {
        return ComplexBusinessEvent.builder()
                .booleanField1(i % 2 == 0)
                .booleanField2(i % 3 == 0)
                .booleanField3(i % 4 == 0)
                .booleanField4(i % 5 == 0)
                .booleanField5(i % 6 == 0)
                .booleanField6(i % 7 == 0)
                .booleanField7(i % 8 == 0)
                .booleanField8(i % 9 == 0)
                .booleanField9(i % 10 == 0)
                .booleanField10(i % 100 == 0)
                .byteField1((byte) i)
                .byteField2((byte) (i % 2))
                .byteField3((byte) (i % 3))
                .byteField4((byte) (i % 4))
                .byteField5((byte) (i % 5))
                .byteField6((byte) (i % 6))
                .byteField7((byte) (i % 7))
                .byteField8((byte) (i % 8))
                .byteField9((byte) (i % 9))
                .byteField10((byte) (i % 10))
                .intField1(i)
                .intField2(i << 1)
                .intField3(i << 2)
                .intField4(i << 3)
                .intField5(i << 4)
                .intField6(i << 5)
                .intField7(i << 6)
                .intField8(i << 7)
                .intField9(i << 8)
                .intField10(i << 9)
                .longField1((long) i)
                .longField2(((long) i << 1))
                .longField3(((long) i << 2))
                .longField4((long) i << 3)
                .longField5((long) i << 4)
                .longField6((long) i << 5)
                .longField7((long) i << 6)
                .longField8((long) i << 7)
                .longField9((long) i << 8)
                .longField10((long) i << 9)
                .floatField1((float) i)
                .floatField2((float) i / 2)
                .floatField3((float) i / 3)
                .floatField4((float) i / 4)
                .floatField5((float) i / 5)
                .floatField6((float) i / 6)
                .floatField7((float) i / 7)
                .floatField8((float) i / 8)
                .floatField9((float) i / 9)
                .floatField10((float) i / 10)
                .build();
    }

    // --- KAFKA ---

    abstract static class TestKafka {
        protected abstract String getBrokerAddress();

        public Properties getProducerProps() {
            Properties producerProps = new Properties();
            producerProps.putAll(KafkaTestUtils.producerProps(getBrokerAddress()));
            producerProps.put(ProducerConfig.ACKS_CONFIG, "all");
            producerProps.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, "120000");
            producerProps.put(ProducerConfig.LINGER_MS_CONFIG, "0");
            producerProps.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, "60000");
            producerProps.put(ProducerConfig.RECEIVE_BUFFER_CONFIG, "32768");
            producerProps.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "30000");
            producerProps.put(ProducerConfig.SEND_BUFFER_CONFIG, "131072");
            producerProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
            producerProps.put(ProducerConfig.RETRIES_CONFIG, "3");
            producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
            return producerProps;
        }

        public Properties getConsumerProps() {
            Properties consumerProps = new Properties();
            consumerProps.putAll(KafkaTestUtils.consumerProps(getBrokerAddress(), TEST_GROUP + "." + System.currentTimeMillis(), "true"));
            consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            return consumerProps;
        }
    }

    static class EmbeddedKafka extends TestKafka {
        private final EmbeddedKafkaBroker embeddedKafkaBroker;

        public EmbeddedKafka(EmbeddedKafkaBroker embeddedKafkaBroker) {
            this.embeddedKafkaBroker = embeddedKafkaBroker;
        }

        @Override
        public String getBrokerAddress() {
            return embeddedKafkaBroker.getBrokersAsString();
        }
    }

    static class ExternalKafka extends TestKafka {
        private final String brokerAddress;

        public ExternalKafka(String brokerAddress) {
            this.brokerAddress = brokerAddress;
        }

        @Override
        public String getBrokerAddress() {
            return this.brokerAddress;
        }
    }
}