package ru.vtb.uasp.beg.producer.validator;

public class TransportStateValidationException extends RuntimeException {
    public TransportStateValidationException(Throwable cause) {
        super(cause);
    }

    public TransportStateValidationException(String message) {
        super(message);
    }

    public TransportStateValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
