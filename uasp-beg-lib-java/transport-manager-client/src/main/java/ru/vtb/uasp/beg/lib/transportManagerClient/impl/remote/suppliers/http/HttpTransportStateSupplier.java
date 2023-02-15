package ru.vtb.uasp.beg.lib.transportManagerClient.impl.remote.suppliers.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportState;

import java.time.Duration;

public class HttpTransportStateSupplier {

    private final String AUTHORIZATION = "Authorization";
    private final String BEARER = "Bearer";

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final HttpClient httpClient;

    public HttpTransportStateSupplier(String baseUrl, String jwtToken, Duration responseTimeout) {
        httpClient = HttpClient.create()
                .followRedirect(true)
                .headers(headers -> headers.add(AUTHORIZATION, BEARER + " " + jwtToken))
                .responseTimeout(responseTimeout)
                .baseUrl(baseUrl);

        httpClient.warmup().block();
    }

    public Mono<TransportState> get(String businessEventName) {
        String uri = "/api/transport/names/" + businessEventName;
        return httpClient.get()
                .uri(uri)
                .responseContent()
                .aggregate()
                .asString()
                .map(this::deserialize);
    }

    private TransportState deserialize(String data) {
        try {
            return MAPPER.readValue(data, TransportState.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
