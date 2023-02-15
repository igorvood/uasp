package ru.vtb.uasp.beg.producer.validator;

public class SchemaValidationException extends RuntimeException {
    public SchemaValidationException(Throwable cause) {
        super(cause);
    }

    public SchemaValidationException(String message) {
        super(message);
    }

    public SchemaValidationException(String message, Throwable cause) {
        super(message, cause);
    }

}

