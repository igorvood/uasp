package ru.vtb.uasp.beg.lib.sample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.vtb.uasp.beg.lib.sample.model.SampleBusinessEvent;
import ru.vtb.uasp.beg.producer.BusinessEventProducer;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class ApplicationService {

    public static final int COUNT = 100_000;

    @Resource
    private BusinessEventProducer businessEventProducer;

    private int x = 0;
    private long start;
    private final AtomicInteger receivedCount = new AtomicInteger(0);
    private final AtomicLong receivedTs = new AtomicLong(0);

    @EventListener(ApplicationReadyEvent.class)
    public void produceBusinessEvent() {
        // Waiting for consumer initialization
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
        }

        log.debug("*** Start producing {} events...", COUNT);
        start = System.currentTimeMillis();
        while (x < COUNT) {
            x++;
            SampleBusinessEvent sampleBusinessEvent = new SampleBusinessEvent(x);
            businessEventProducer.send(String.valueOf(sampleBusinessEvent.getX()), sampleBusinessEvent);
        }
        long end = System.currentTimeMillis();
        double seconds = (double) (end - start) / 1000;
        log.debug("*** Produced {} events for {} seconds", COUNT, seconds);
    }

    public void consume(SampleBusinessEvent sampleBusinessEvent) {
        int receivedSoFar = receivedCount.incrementAndGet();
        if (log.isDebugEnabled()) {
            long currentTs = System.currentTimeMillis();
            // Display new consumed events with interval 100 ms
            if (currentTs - receivedTs.get() >= 100 || sampleBusinessEvent.getX() == COUNT) {
                log.debug("Received: {}", sampleBusinessEvent);
                receivedTs.set(currentTs);
            }
        }
        if (sampleBusinessEvent.getX() == COUNT) {
            long end = System.currentTimeMillis();
            log.debug("*** Consumed {} events. Completed in {} seconds", receivedSoFar, (double) (end - start) / 1000);
            System.exit(0);
        }
    }
}