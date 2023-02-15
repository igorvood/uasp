package ru.vtb.uasp.beg.lib.transportManagerClient.impl.remote.suppliers.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportState;

import java.util.Collections;
import java.util.Properties;

public class KafkaTransportStateSupplier {

    private final ReceiverOptions<String, TransportState> receiverOptions;

    public KafkaTransportStateSupplier(Properties properties, String address, String group, String topic) {
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonTransportStateDeserializer.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, address);

        this.receiverOptions = ReceiverOptions
                .<String, TransportState>create(properties)
                .subscription(Collections.singletonList(topic));
    }

    public Flux<TransportState> getTransportStates() {
        return KafkaReceiver
                .create(receiverOptions)
                .receiveAutoAck()
                .flatMap(flux -> flux)
                .map(ConsumerRecord::value);
    }
}
