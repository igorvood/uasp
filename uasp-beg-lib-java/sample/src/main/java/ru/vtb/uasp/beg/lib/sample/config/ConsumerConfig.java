package ru.vtb.uasp.beg.lib.sample.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import ru.vtb.uasp.beg.consumer.BusinessEventSubscriber;
import ru.vtb.uasp.beg.lib.sample.ApplicationService;
import ru.vtb.uasp.beg.lib.sample.model.SampleBusinessEvent;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportManagerClient;

import java.util.Properties;
import java.util.UUID;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static ru.vtb.uasp.beg.lib.core.kafka.ConfigUtils.getKafkaProducerProperties;

@EnableKafka
@Configuration
@Slf4j
public class ConsumerConfig {

    @Bean
    public BusinessEventSubscriber businessEventConsumer(
            TransportManagerClient transportManagerClient,
            ProducerProperties producerProperties,
            ApplicationService applicationService) {
        Properties properties = new Properties();
        // Default properties
        properties.put(GROUP_ID_CONFIG, UUID.randomUUID().toString());
        properties.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // Extend or override with configuration properties
        properties.putAll(getKafkaProducerProperties(MapUtils.toProperties(producerProperties.getKafka())));

        BusinessEventSubscriber businessEventSubscriber = new BusinessEventSubscriber(transportManagerClient);
        businessEventSubscriber.registerConsumer(SampleBusinessEvent.class, applicationService::consume);
        businessEventSubscriber.subscribe(properties);

        log.debug("Consumer for {} created.", SampleBusinessEvent.class.getSimpleName());

        return businessEventSubscriber;
    }
}
