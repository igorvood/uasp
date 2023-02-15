package ru.vtb.uasp.beg.producer.validator.json;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.ConfluentSchema;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class JsonSchemaValidator {
    private final Map<SchemaId, Schema> schemas = new ConcurrentHashMap<>();

    public void validate(ConfluentSchema confluentSchema, String data) {
        SchemaId businessEventId = new SchemaId(confluentSchema.getSubject(), confluentSchema.getVersion());
        Schema schema = schemas.computeIfAbsent(
                businessEventId,
                notUsed -> SchemaLoader.load(new JSONObject(confluentSchema.getSchema()))
        );
        schema.validate(new JSONObject(new JSONTokener(data)));
    }

    private static class SchemaId {
        private final String businessEventName;
        private final int businessEventVersion;

        public SchemaId(String businessEventName, int businessEventVersion) {
            this.businessEventName = businessEventName;
            this.businessEventVersion = businessEventVersion;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SchemaId schemaId = (SchemaId) o;
            return businessEventVersion == schemaId.businessEventVersion
                    && Objects.equals(businessEventName, schemaId.businessEventName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(businessEventName, businessEventVersion);
        }
    }
}
