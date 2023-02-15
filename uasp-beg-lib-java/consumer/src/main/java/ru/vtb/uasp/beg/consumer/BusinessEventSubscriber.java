package ru.vtb.uasp.beg.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import ru.vtb.uasp.beg.lib.core.BusinessEvent;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportManagerClient;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportState;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.vtb.uasp.beg.lib.core.BusinessEventValidator.requireAnnotation;

public class BusinessEventSubscriber {
    private static final Logger log = LoggerFactory.getLogger(BusinessEventSubscriber.class);

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final TransportManagerClient transportManagerClient;

    // businessEventName -> SubscriptionContext
    private final Map<String, SubscriptionContext<?>> nameToContext = new ConcurrentHashMap<>();

    // topicName -> SubscriptionContext
    private final Map<String, SubscriptionContext<?>> topicToContext = new ConcurrentHashMap<>();

    public BusinessEventSubscriber(TransportManagerClient transportManagerClient) {
        this.transportManagerClient = transportManagerClient;
    }

    public <EVENT> void registerConsumer(Class<EVENT> eventClass, BusinessEventConsumer<EVENT> consumer) {
        BusinessEvent businessEventAnnotation = requireAnnotation(eventClass);
        nameToContext.put(businessEventAnnotation.name(), new SubscriptionContext<>(eventClass, consumer));
    }

    public void subscribe(Properties properties) {
        List<Mono<TransportState>> getTransportStateMonos = nameToContext.keySet().stream()
                .map(transportManagerClient::getTransportState)
                .collect(Collectors.toList());

        Mono.zip(getTransportStateMonos, BusinessEventSubscriber::combineToList)
                .subscribe(transportStates -> {
                    transportStates.forEach(transportState -> {
                        String businessEventName = transportState.getName();
                        String topicName = transportState.getTopicName();
                        SubscriptionContext<?> subscriptionContext = nameToContext.get(businessEventName);
                        topicToContext.put(topicName, subscriptionContext);
                    });
                    Set<String> topics = topicToContext.keySet();

                    ReceiverOptions<String, String> receiverOptions = ReceiverOptions
                            .<String, String>create(properties)
                            .subscription(topics);
                    KafkaReceiver
                            .create(receiverOptions)
                            .receiveAutoAck()
                            .concatMap(Function.identity())
                            .doOnNext(this::handle)
                            .doOnError((e) -> log.debug("consuming ERROR"))
                            .subscribe();
                });
    }

    private static List<TransportState> combineToList(Object... transportStatesArray) {
        return Arrays.stream(transportStatesArray)
                .map(t -> (TransportState) t)
                .collect(Collectors.toList());
    }

    public void handle(ConsumerRecord<String, String> consumerRecord) {
        String topic = consumerRecord.topic();
        String json = consumerRecord.value();
        topicToContext.get(topic).handleJson(json);
    }

    protected static class SubscriptionContext<EVENT> {
        private final Class<EVENT> eventClass;
        private final BusinessEventConsumer<EVENT> consumer;

        public SubscriptionContext(Class<EVENT> eventClass, BusinessEventConsumer<EVENT> consumer) {
            this.eventClass = eventClass;
            this.consumer = consumer;
        }

        public void handleJson(String json) {
            EVENT event;
            try {
                event = MAPPER.readValue(json, eventClass);
            } catch (JsonProcessingException e) {
                log.debug("consuming ERROR");
                return;
//                throw new RuntimeException(e);
            }
            consumer.consume(event);
        }
    }
}
