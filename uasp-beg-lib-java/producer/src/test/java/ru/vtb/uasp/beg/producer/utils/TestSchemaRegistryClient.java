package ru.vtb.uasp.beg.producer.utils;

import reactor.core.publisher.Mono;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.ConfluentSchema;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.SchemaRegistryClient;

public class TestSchemaRegistryClient implements SchemaRegistryClient {
    @Override
    public Mono<ConfluentSchema> getSchema(String subject, String version) {
        return null;
    }
}
