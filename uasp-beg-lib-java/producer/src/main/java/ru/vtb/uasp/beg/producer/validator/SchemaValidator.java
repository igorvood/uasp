package ru.vtb.uasp.beg.producer.validator;

import ru.vtb.uasp.beg.lib.schemaRegistryClient.ConfluentSchema;
import ru.vtb.uasp.beg.producer.validator.json.JsonSchemaValidator;

public class SchemaValidator {

    private final static JsonSchemaValidator VALIDATOR = new JsonSchemaValidator();

    public void validate(String data, ConfluentSchema confluentSchema) throws SchemaValidationException {
        try {
            validateInternal(confluentSchema, data);
        } catch (Exception e) {
            throw new SchemaValidationException(e);
        }
    }

    private void validateInternal(ConfluentSchema confluentSchema, String data) {
        if (confluentSchema.getSchemaType().equalsIgnoreCase("json")) {
            VALIDATOR.validate(confluentSchema, data);
            return;
        }
        throw new RuntimeException("Unknown schema type " + confluentSchema);
    }
}
