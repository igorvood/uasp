package ru.vtb.uasp.beg.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;
import ru.vtb.uasp.beg.lib.core.BusinessEvent;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.ConfluentSchema;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.SchemaRegistryClient;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportManagerClient;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportState;
import ru.vtb.uasp.beg.producer.validator.SchemaValidator;
import ru.vtb.uasp.beg.producer.validator.TransportStateValidationException;

import java.util.Properties;
import java.util.function.Consumer;

import static ru.vtb.uasp.beg.lib.core.BusinessEventValidator.requireAnnotation;
import static ru.vtb.uasp.beg.producer.BusinessEventProducer.TraceMsg.*;

public class BusinessEventProducer {
    private static final Logger log = LoggerFactory.getLogger(BusinessEventProducer.class);
    private final static ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final static StringSerializer SERIALIZER = new StringSerializer();

    private final TransportManagerClient transportStateManager;
    private final SchemaRegistryClient schemaRegistryClient;
    private final SchemaValidator schemaValidator = new SchemaValidator();
    private Consumer<ProducerResult> producerResultConsumer;
    private Consumer<Throwable> errorConsumer;

    private final Sinks.Many<SenderRecord<String, String, Object>> publishingSink = Sinks.many().unicast().onBackpressureBuffer();

    protected BusinessEventProducer(
            TransportManagerClient transportStateManager,
            SchemaRegistryClient schemaRegistryClient,
            Properties kafkaProperties,
            Consumer<ProducerResult> producerResultConsumer,
            Consumer<Throwable> errorConsumer) {
        this.transportStateManager = transportStateManager;
        this.schemaRegistryClient = schemaRegistryClient;
        this.producerResultConsumer = producerResultConsumer;
        this.errorConsumer = errorConsumer;

        SenderOptions<String, String> senderOptions = SenderOptions.<String, String>create(kafkaProperties)
                .withKeySerializer(SERIALIZER)
                .withValueSerializer(SERIALIZER)
                .maxInFlight(1024)
                .stopOnError(false);
        KafkaSender.create(senderOptions)
                .send(publishingSink.asFlux())
                .subscribe(this::processSenderResult, this::processError, null);
    }

    public void setProducerResultConsumer(Consumer<ProducerResult> producerResultConsumer) {
        this.producerResultConsumer = producerResultConsumer;
    }

    public void setErrorConsumer(Consumer<Throwable> errorConsumer) {
        this.errorConsumer = errorConsumer;
    }

    /**
     * Sends a business event with key=null to Kafka
     */
    public void send(Object businessEvent) {
        send(null, businessEvent);
    }

    /**
     * Sends a business event with provided key to Kafka
     */
    public void send(String key, Object businessEvent) {
        BusinessEvent annotation = requireAnnotation(businessEvent.getClass());
        START_LOG.apply(annotation);
        try {
            transportStateManager.getTransportState(annotation.name())
                    .doOnNext(transportState -> {
                        if (!transportState.isEnabled()) {
                            SKIP_LOG.apply(annotation);
                            throw new TransportStateValidationException("Event sending disabled");
                        }
                    })
                    .flatMap(transportState -> createContext(transportState, annotation.name(), annotation.version()))
                    .doOnNext(context -> {
                        String serializedEvent = serialize(businessEvent);
                        schemaValidator.validate(serializedEvent, context.getConfluentSchema());
                        sendToKafka(key, serializedEvent, context.getTransportState().getTopicName(), businessEvent);
                        SENT_LOG.apply(annotation);
                    })
                    .subscribe(null,
                            throwable -> sendProducerResult(businessEvent, false, (Exception) throwable),
                            null);
            FINISH_LOG.apply(annotation);
        } catch (Exception e) {
            ERROR_LOG.apply(annotation);
            if (errorConsumer != null) errorConsumer.accept(e);
        }
    }

    private void sendToKafka(String key, String value, String topic, Object businessEvent) {
        SenderRecord<String, String, Object> senderRecord =
                SenderRecord.create(topic, null, null, key, value, businessEvent);
        // Note: Using tryEmitNext here in order to prevent an overflow issue
        // publishingSink.emitNext(senderRecord, Sinks.EmitFailureHandler.FAIL_FAST);
        publishingSink.tryEmitNext(senderRecord);
    }

    private String serialize(Object businessEvent) {
        try {
            return MAPPER.writeValueAsString(businessEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Mono<SendingContext> createContext(TransportState transportState, String businessEventName, String businessEventVersion) {
        return schemaRegistryClient.getSchema(businessEventName, businessEventVersion)
                .map(confluentSchema -> new SendingContext(transportState, confluentSchema));
    }

    /**
     * Handles a result of sending data into Kafka
     *
     * @param result sending result
     */
    private void processSenderResult(SenderResult<?> result) {
        sendProducerResult(result.correlationMetadata(), result.exception() == null, result.exception());
    }

    /**
     * Sends result of producing a business event into Kafka
     *
     * @param businessEvent business event
     * @param success       whether it was sent successfully
     * @param exception     exception
     */
    private void sendProducerResult(Object businessEvent, boolean success, Exception exception) {
        if (producerResultConsumer != null) {
            producerResultConsumer.accept(new ProducerResult(businessEvent, success, exception));
        }
    }

    /**
     * Handles a error from KafkaSender
     *
     * @param error throwable
     */
    private void processError(Throwable error) {
        if (errorConsumer != null) {
            errorConsumer.accept(error);
        }
    }

    private static class SendingContext {
        private final TransportState transportState;
        private final ConfluentSchema confluentSchema;

        public SendingContext(TransportState transportState, ConfluentSchema confluentSchema) {
            this.transportState = transportState;
            this.confluentSchema = confluentSchema;
        }

        public TransportState getTransportState() {
            return transportState;
        }

        public ConfluentSchema getConfluentSchema() {
            return confluentSchema;
        }
    }

    enum TraceMsg {
        START_LOG("START event sending"),
        FINISH_LOG("FINISH event sending"),
        ERROR_LOG("Event sending ERROR"),
        SKIP_LOG("Event sending DISABLED"),
        SENT_LOG("Event SENT");
        private final static String SUFFIX = " / name: {}, v: {}]";
        private final String msg;

        TraceMsg(String msg) {
            this.msg = msg;
        }

        void apply(BusinessEvent event) {
            if (log.isTraceEnabled()) {
                log.trace(msg + SUFFIX, event.name(), event.version());
            }
        }
    }
}
