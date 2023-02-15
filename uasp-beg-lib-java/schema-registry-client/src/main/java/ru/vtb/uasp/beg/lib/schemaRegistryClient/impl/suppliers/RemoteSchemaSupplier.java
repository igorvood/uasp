package ru.vtb.uasp.beg.lib.schemaRegistryClient.impl.suppliers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.ConfluentSchema;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.impl.SchemaSupplier;

import java.time.Duration;

public class RemoteSchemaSupplier implements SchemaSupplier {

    private final HttpClient httpClient;
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public RemoteSchemaSupplier(String baseUrl, String jwtToken, Duration responseTimeout) {
        httpClient = HttpClient.create()
                .followRedirect(true)
                .headers(headers -> headers.add("Authorization", "Bearer " + jwtToken))
                .responseTimeout(responseTimeout)
                .baseUrl(baseUrl);

        httpClient.warmup().block();
    }

    @Override
    public Mono<ConfluentSchema> fetch(String subject, String version) {
        String uri = "/subjects/{subject}/versions/{version}"
                .replace("{subject}", subject)
                .replace("{version}", version);

        return httpClient.get()
                .uri(uri)
                .responseContent()
                .aggregate()
                .asString()
                .map(this::deserialize);
    }

    private ConfluentSchema deserialize(String data) {
        try {
            return MAPPER.readValue(data, ConfluentSchema.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
