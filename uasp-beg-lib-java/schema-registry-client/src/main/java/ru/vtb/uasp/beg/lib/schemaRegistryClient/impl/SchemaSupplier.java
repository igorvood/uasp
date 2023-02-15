package ru.vtb.uasp.beg.lib.schemaRegistryClient.impl;

import reactor.core.publisher.Mono;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.ConfluentSchema;

public interface SchemaSupplier {

    Mono<ConfluentSchema> fetch(String subject, String version);
}
