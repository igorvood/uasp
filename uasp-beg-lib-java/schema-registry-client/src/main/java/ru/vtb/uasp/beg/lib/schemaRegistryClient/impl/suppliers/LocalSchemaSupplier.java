package ru.vtb.uasp.beg.lib.schemaRegistryClient.impl.suppliers;

import reactor.core.publisher.Mono;
import ru.vtb.uasp.beg.lib.core.utils.ResourceUtils;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.ConfluentSchema;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.impl.SchemaSupplier;

import java.util.Collections;
import java.util.Objects;

public class LocalSchemaSupplier implements SchemaSupplier {

    private final String schemaRegistryDirectoryPath;

    public LocalSchemaSupplier(String schemaRegistryDirectoryPath) {
        Objects.requireNonNull(schemaRegistryDirectoryPath, "SchemaRegistry directory must be specified");
        if (!ResourceUtils.isResourcePathExists(schemaRegistryDirectoryPath))
            throw new RuntimeException("Directory expected as LocalSchemaRegistry constructor argument");
        this.schemaRegistryDirectoryPath = schemaRegistryDirectoryPath;
    }

    @Override
    public Mono<ConfluentSchema> fetch(String subject, String version) {
        String schemaFileName = subject + "-" + version + ".json";
        return Mono.fromCallable(() -> ResourceUtils.getResourceAsString(schemaRegistryDirectoryPath, schemaFileName))
                .map(schema -> createConfluentSchema(subject, version, schema));
    }

    private static ConfluentSchema createConfluentSchema(String subject, String version, String schema) {
        ConfluentSchema confluentSchema = new ConfluentSchema();
        confluentSchema.setSubject(subject);
        confluentSchema.setVersion(Integer.parseInt(version));
        confluentSchema.setId(0);
        confluentSchema.setSchemaType("json");
        confluentSchema.setReferences(Collections.emptyList());
        confluentSchema.setSchema(schema);
        return confluentSchema;
    }
}
