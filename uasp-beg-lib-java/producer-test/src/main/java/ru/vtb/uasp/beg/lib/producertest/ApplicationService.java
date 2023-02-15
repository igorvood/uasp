package ru.vtb.uasp.beg.lib.producertest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.vtb.uasp.beg.lib.producertest.config.ProducerProperties;
import ru.vtb.uasp.beg.lib.producertest.model.FirstBusinessEvent;
import ru.vtb.uasp.beg.lib.producertest.model.SecondBusinessEvent;
import ru.vtb.uasp.beg.producer.BusinessEventProducer;
import ru.vtb.uasp.beg.producer.ProducerResult;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Supplier;
import java.util.stream.IntStream;

@Slf4j
@Service
@Profile("lt")
public class ApplicationService {

    @Resource
    private ProducerProperties producerProperties;

    @Resource
    private BusinessEventProducer businessEventProducer;

    private final static int[] BatchSizes = {100, 300, 500, 700, 1000, 2000, 3000, 5000, 7000, 10000, 20000, 50000, 100000, 200000, 500000, 700000, 1000000};
    private final ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
    private int runCount;
    private CountDownLatch latch;
    private final Map<Class<?>, Integer> eventCounters = new HashMap<>();

    @EventListener(ApplicationReadyEvent.class)
    public void startProducing() {
        businessEventProducer.setProducerResultConsumer(this::onSendValue);
        businessEventProducer.setErrorConsumer(Throwable::printStackTrace);
        service.scheduleWithFixedDelay(this::produce, 0, producerProperties.getBatchInterval().toMillis(), TimeUnit.MILLISECONDS);
    }

    private void onSendValue(ProducerResult r) {
        if (!r.isSuccess()) {
            log.debug("Error send: {}. Exception: {}", r.getBusinessEvent(), r.getException().getMessage());
        }
        latch.countDown();
    }

    private void produce() {
        runCount++;
        final Supplier<Class<?>> eventTypeSupplier = producerProperties.isRandomEvent()
                ? EventType.getRandomEventTypeSupplier
                : EventType.getSingleEventTypeSupplier;
        final int batchSize = producerProperties.isRandomBatchSize()
                ? BatchSizes[ThreadLocalRandom.current().nextInt(0, BatchSizes.length)]
                : producerProperties.getBatchSize();
        final Map<Class<?>, Integer> sentCounters = new HashMap<>();
        eventCounters.clear();

        log.debug("Starting batch #{} to produce {} events", runCount, batchSize);
        this.latch = new CountDownLatch(batchSize);
        long start = System.currentTimeMillis();
        IntStream.range(0, batchSize)
                .mapToObj(i -> generateEvent(eventTypeSupplier.get(), i))
                .peek(e -> sentCounters.compute(e.getClass(), (k, v) -> (v == null) ? 1 : v + 1))
                .forEach(e -> businessEventProducer.send(eventKey(e), e));
        log.debug("{} events sent to producer queue", batchSize);
        try {
            latch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();

        int sentTotal = sentCounters.values().stream().mapToInt(x -> x).sum();
        double duration = (double) (end - start) / 1000;
        sentCounters.forEach((k, v) -> log.debug("Sent {} events of type {}", v, k.getSimpleName()));
        log.debug("Totally produced {} ({} confirmed) events for {} seconds, tps={}", sentTotal, sentTotal - latch.getCount(), duration, sentTotal / duration);

        // If all batches completed then exit
        if (runCount >= producerProperties.getBatches()) {
            service.shutdown();
        }
    }

    private Object generateEvent(Class<?> eventClass, int i) {
        Integer id = eventCounters.compute(eventClass, (k, v) -> (v == null) ? 1 : v + 1);
        if (Objects.equals(FirstBusinessEvent.class, eventClass)) {
            return FirstBusinessEvent.builder()
                    .id(id)
                    .name("Event " + i)
                    .description(FirstBusinessEvent.class.getSimpleName())
                    .timestamp(System.currentTimeMillis())
                    .build();
        }
        if (Objects.equals(SecondBusinessEvent.class, eventClass)) {
            return SecondBusinessEvent.builder()
                    .id(id)
                    .name("Event " + i)
                    .intValue(i)
                    .doubleValue((double) i / 10)
                    .byteValue((byte) i)
                    .timestamp(System.currentTimeMillis())
                    .build();
        }
        throw new IllegalStateException("Unsupported event class " + eventClass);
    }

    private String eventKey(Object event) {
        if (event instanceof FirstBusinessEvent) {
            return String.valueOf(((FirstBusinessEvent) event).getId());
        } else if (event instanceof SecondBusinessEvent) {
            return String.valueOf(((SecondBusinessEvent) event).getId());
        }
        return null;
    }

    private enum EventType {
        FIRST(FirstBusinessEvent.class),
        SECOND(SecondBusinessEvent.class);

        private final Class<?> eventClass;

        EventType(Class<?> eventClass) {
            this.eventClass = eventClass;
        }

        public static Supplier<Class<?>> getSingleEventTypeSupplier =
                () -> FIRST.eventClass;

        public static Supplier<Class<?>> getRandomEventTypeSupplier =
                () -> values()[ThreadLocalRandom.current().nextInt(0, values().length)].eventClass;
    }
}
