package ru.vtb.uasp.beg.lib.transportManagerClient.impl.remote;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportManagerClient;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportState;
import ru.vtb.uasp.beg.lib.transportManagerClient.impl.remote.suppliers.http.HttpTransportStateSupplier;
import ru.vtb.uasp.beg.lib.transportManagerClient.impl.remote.suppliers.kafka.KafkaTransportStateSupplier;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RemoteTransportManagerClient implements TransportManagerClient {
    private final static Logger log = LoggerFactory.getLogger(RemoteTransportManagerClient.class);
    private final static Duration DEFAULT_UPDATE_INTERVAL = Duration.of(1, ChronoUnit.HOURS);

    private final Map<String, TransportState> transportStates = new ConcurrentHashMap<>();

    private final HttpTransportStateSupplier httpTransportStateSupplier;
    private final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(1);

    public RemoteTransportManagerClient(HttpTransportStateSupplier httpTransportStateSupplier,
                                        KafkaTransportStateSupplier kafkaTransportStateSupplier) {
        this(httpTransportStateSupplier, kafkaTransportStateSupplier, DEFAULT_UPDATE_INTERVAL);
    }

    public RemoteTransportManagerClient(HttpTransportStateSupplier httpTransportStateSupplier,
                                        KafkaTransportStateSupplier kafkaTransportStateSupplier,
                                        Duration updateInterval) {
        this.httpTransportStateSupplier = httpTransportStateSupplier;
        kafkaTransportStateSupplier.getTransportStates().subscribe(this::putTransportStateToStore);
        scheduledExecutor.scheduleAtFixedRate(this::updateTransportStates,
                updateInterval.getSeconds(), updateInterval.getSeconds(), TimeUnit.SECONDS);
    }

    /**
     * Updates transport states in accordance to schedule
     */
    private void updateTransportStates() {
        transportStates.forEach((k, oldValue) -> transportStates.put(k, onFetch(loadFromHttp(k))));
    }

    /**
     * Returns transport state either existing from the map or requesting it by http
     *
     * @param businessEventName name of business event
     * @return
     */
    @Override
    public Mono<TransportState> getTransportState(String businessEventName) {
        return Mono.just(transportStates.compute(businessEventName, (k, oldValue) -> oldValue != null ? oldValue
                : onFetch(loadFromHttp(k))));
    }

    /**
     * Loads transport state by http
     *
     * @param businessEventName name of business event
     * @return
     */
    private TransportState loadFromHttp(String businessEventName) {
        return httpTransportStateSupplier.get(businessEventName).block();
    }

    /**
     * Updates transport state from Kafka message
     *
     * @param transportState entry
     * @return
     */
    private TransportState putTransportStateToStore(TransportState transportState) {
        return onFetch(transportStates.compute(
                transportState.getName(),
                (k, previousTransportState) -> chooseActualTransportState(transportState, previousTransportState)));
    }

    private TransportState chooseActualTransportState(TransportState state1, TransportState state2) {
        if (state1 == null) return state2;
        if (state2 == null) return state1;
        return state1.getLastUpdateTimestamp() > state2.getLastUpdateTimestamp()
                ? state1
                : state2;
    }

    /**
     * Outputs received transport state
     *
     * @param transportState entry
     * @return
     */
    private TransportState onFetch(TransportState transportState) {
        log.debug("Got " + transportState);
        return transportState;
    }

}
