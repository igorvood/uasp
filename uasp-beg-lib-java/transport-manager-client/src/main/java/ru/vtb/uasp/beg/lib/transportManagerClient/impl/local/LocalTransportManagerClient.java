package ru.vtb.uasp.beg.lib.transportManagerClient.impl.local;

import reactor.core.publisher.Mono;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportManagerClient;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportState;

import java.util.function.Function;

public class LocalTransportManagerClient implements TransportManagerClient {

    // Resolver "Business Event name -> Transport State"
    private final Function<String, TransportState> transportStateResolver;

    public LocalTransportManagerClient(Function<String, TransportState> transportStateResolver) {
        this.transportStateResolver = transportStateResolver;
    }

    @Override
    public Mono<TransportState> getTransportState(String businessEventName) {
        TransportState transportState = transportStateResolver.apply(businessEventName);
        if (transportState != null) return Mono.just(transportState);
        throw new RuntimeException("Transport state not fount for " + businessEventName);
    }
}
