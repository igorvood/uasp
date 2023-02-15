package ru.vtb.uasp.beg.lib.schemaRegistryClient.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.ConfluentSchema;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.SchemaRegistryClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SchemaRegistryClientImpl implements SchemaRegistryClient {
    private final static Logger log = LoggerFactory.getLogger(SchemaRegistryClientImpl.class);

    private final SchemaSupplier schemaSupplier;
    private final Map<SchemaId, ConfluentSchema> confluentSchemas = new ConcurrentHashMap<>();

    public SchemaRegistryClientImpl(SchemaSupplier schemaSupplier) {
        this.schemaSupplier = schemaSupplier;
    }

    @Override
    public Mono<ConfluentSchema> getSchema(String subject, String version) {
        SchemaId schemaId = new SchemaId(subject, version);
        return Mono.just(confluentSchemas.compute(schemaId, (k, oldValue) -> oldValue != null ? oldValue
                : onFetch(schemaId, schemaSupplier.fetch(subject, version).block())));
    }

    private ConfluentSchema onFetch(SchemaId schemaId, ConfluentSchema schema) {
        log.debug("Got " + schemaId);
        return schema;
    }

}
