package ru.vtb.uasp.beg.lib.transportManagerClient;

import reactor.core.publisher.Mono;

public interface TransportManagerClient {
    Mono<TransportState> getTransportState(String businessEventName);
}
