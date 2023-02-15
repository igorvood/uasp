package ru.vtb.uasp.beg.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.vtb.uasp.beg.consumer.BusinessEventSubscriber.SubscriptionContext;
import ru.vtb.uasp.beg.consumer.utils.TestBusinessEvent;
import ru.vtb.uasp.beg.consumer.utils.TestTransportManagerClient;
import ru.vtb.uasp.beg.lib.core.BusinessEvent;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportManagerClient;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportState;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class BusinessEventSubscriberTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private BusinessEventSubscriber businessEventSubscriber;
    private TransportManagerClient transportManagerClient;

    private enum TestEvent {
        TEST_EVENT_1(TestEvent1.class, "test_event_1", "test_event_1.topic"),
        TEST_EVENT_2(TestEvent2.class, "test_event_2", "test_event_2.topic"),
        TEST_EVENT_3(TestEvent3.class, "test_event_3", "test_event_3.topic");

        public final Class<? extends TestBusinessEvent> clazz;
        public final String name;
        public final String topic;

        TestEvent(Class<? extends TestBusinessEvent> clazz, String name, String topic) {
            this.clazz = clazz;
            this.name = name;
            this.topic = topic;
        }
    }

    @BeforeEach
    void setUp() {
        transportManagerClient = new TestTransportManagerClient(BusinessEventSubscriberTest::createTransportState);
        businessEventSubscriber = new BusinessEventSubscriber(transportManagerClient);
    }

    @Test
    void registerConsumerTest() {
        Map<String, SubscriptionContext<?>> nameToContext =
                (Map<String, SubscriptionContext<?>>) getFieldValueByName(businessEventSubscriber, "nameToContext");

        // успешнное добавление
        Stream.of(TestEvent.values()).forEach(e -> {
            businessEventSubscriber.registerConsumer(e.clazz, new TestConsumer<>());
            assertTrue(nameToContext.containsKey(e.name));
        });
        assertEquals(TestEvent.values().length, nameToContext.size());

        // ошибка: класс не помечен аннатоцией BusinessEvent
        Stream.of(Object.class, Integer.class, Long.class, List.class)
                .forEach(clazz -> assertThrows(RuntimeException.class, () -> businessEventSubscriber.registerConsumer(clazz, null)));
        assertEquals(TestEvent.values().length, nameToContext.size());
    }

    @Test
    void handleTest() {
        Map<String, SubscriptionContext<?>> topicToContext =
                (Map<String, SubscriptionContext<?>>) getFieldValueByName(businessEventSubscriber, "topicToContext");
        Map<String, TestConsumer<?>> nameToConsumer = Stream.of(TestEvent.values())
                .collect(Collectors.toMap(e -> e.name, e -> new TestConsumer<>()));
        Stream.of(TestEvent.values()).forEach(e ->
                topicToContext.put(e.topic, new SubscriptionContext<>(e.clazz, (TestConsumer) nameToConsumer.get(e.name))));

        for (TestEvent event : TestEvent.values()) {
            int numberOfEvents = ThreadLocalRandom.current().nextInt(10, 100);
            Stream.generate(() -> createEvent(event.clazz))
                    .limit(numberOfEvents)
                    .forEach(e -> businessEventSubscriber.handle(createConsumerRecord(event.topic, e)));
            TestConsumer<?> consumer = nameToConsumer.get(event.name);
            assertEquals(numberOfEvents, consumer.consumedEvents.size());
        }
    }

    private static Object getFieldValueByName(Object object, String name) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @BusinessEvent(name = "test_event_1", version = "1")
    static class TestEvent1 extends TestBusinessEvent {
    }

    @BusinessEvent(name = "test_event_2", version = "1")
    static class TestEvent2 extends TestBusinessEvent {
    }

    @BusinessEvent(name = "test_event_3", version = "1")
    static class TestEvent3 extends TestBusinessEvent {
    }

    static class TestConsumer<EVENT> implements BusinessEventConsumer<EVENT> {
        public final List<EVENT> consumedEvents = new ArrayList<>();

        @Override
        public void consume(EVENT event) {
            consumedEvents.add(event);
        }
    }

    static TransportState createTransportState(String eventName) {
        TransportState result = new TransportState();
        result.setName(eventName);
        result.setTopicName(eventName);
        result.setEnabled(true);
        result.setKafkaAddress(eventName + "_kafka");
        result.setLastUpdateTimestamp(System.currentTimeMillis());
        return result;
    }

    static <EVENT extends TestBusinessEvent> ConsumerRecord<String, String> createConsumerRecord(String eventName, EVENT event) {
        try {
            return new ConsumerRecord<>(eventName, 0, event.id, String.valueOf(event.id), OBJECT_MAPPER.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    static final Map<Class<? extends TestBusinessEvent>, AtomicLong> EVENT_ID_MAP = new HashMap<>();

    static <EVENT extends TestBusinessEvent> EVENT createEvent(Class<EVENT> eventClass) {
        try {
            AtomicLong eventIdCounter = EVENT_ID_MAP.computeIfAbsent(eventClass, c -> new AtomicLong(0));
            Random randomGenerator = new Random();
            EVENT event = eventClass.getDeclaredConstructor().newInstance();
            event.id = eventIdCounter.incrementAndGet();
            event.boolData = randomGenerator.nextBoolean();
            event.intData = randomGenerator.nextInt();
            event.strData = eventClass.getSimpleName();
            return event;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}