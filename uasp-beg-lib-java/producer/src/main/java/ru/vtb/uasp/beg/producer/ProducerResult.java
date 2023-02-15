package ru.vtb.uasp.beg.producer;

/**
 * Encapsulates the result of producing a business event into Kafka by BusinessEventProducer
 */
public class ProducerResult {
    private final boolean success;
    private final Object businessEvent;
    private final Exception exception;

    public ProducerResult(Object businessEvent, boolean success, Exception exception) {
        this.businessEvent = businessEvent;
        this.success = success;
        this.exception = exception;
    }

    public boolean isSuccess() {
        return success;
    }

    public Object getBusinessEvent() {
        return businessEvent;
    }

    public Exception getException() {
        return exception;
    }
}
