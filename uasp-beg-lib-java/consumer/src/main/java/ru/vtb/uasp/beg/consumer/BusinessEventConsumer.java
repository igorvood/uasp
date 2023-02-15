package ru.vtb.uasp.beg.consumer;

public interface BusinessEventConsumer<EVENT> {
    void consume(EVENT event);
}
