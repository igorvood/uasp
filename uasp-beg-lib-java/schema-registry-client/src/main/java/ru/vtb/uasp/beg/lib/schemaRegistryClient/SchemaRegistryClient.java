package ru.vtb.uasp.beg.lib.schemaRegistryClient;

import reactor.core.publisher.Mono;

public interface SchemaRegistryClient {
    Mono<ConfluentSchema> getSchema(String subject, String version);
}
