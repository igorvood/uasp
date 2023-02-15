package ru.vtb.uasp.beg.consumer.utils;

import reactor.core.publisher.Mono;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportManagerClient;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportState;

import java.util.Objects;
import java.util.function.Function;

public class TestTransportManagerClient implements TransportManagerClient {

    private final Function<String, TransportState> transportStateResolver;

    public TestTransportManagerClient(Function<String, TransportState> transportStateResolver) {
        this.transportStateResolver = Objects.requireNonNull(transportStateResolver);
    }

    @Override
    public Mono<TransportState> getTransportState(String eventName) {
        return Mono.just(transportStateResolver.apply(eventName));
    }
}