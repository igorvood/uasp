package ru.vtb.uasp.beg.lib.core;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class BusinessEventValidatorTest {

    @BusinessEvent(name = "good_business_event", version = "1")
    static class GoodBusinessEvent {
    }

    static class NotBusinessEvent {
    }

    @Test
    void requireAnnotationTest() {
        AtomicReference<BusinessEvent> annotation = new AtomicReference<>(null);

        GoodBusinessEvent goodBusinessEvent = new GoodBusinessEvent();
        assertDoesNotThrow(() -> annotation.set(BusinessEventValidator.requireAnnotation(goodBusinessEvent.getClass())));
        assertNotNull(annotation.get());
        assertEquals("good_business_event", annotation.get().name());
        assertEquals("1", annotation.get().version());

        assertThrows(RuntimeException.class, () -> BusinessEventValidator.requireAnnotation(NotBusinessEvent.class));
    }
}