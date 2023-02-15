package ru.vtb.uasp.beg.lib.sample.config;

import lombok.SneakyThrows;
import org.apache.commons.collections.MapUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ObjectUtils;
import ru.vtb.uasp.beg.lib.core.kafka.ConfigUtils;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.SchemaRegistryClient;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.impl.SchemaRegistryClientBuilder;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportManagerClient;
import ru.vtb.uasp.beg.lib.transportManagerClient.impl.TransportManagerClientBuilder;
import ru.vtb.uasp.beg.lib.transportManagerClient.impl.local.LocalTransportManagerClientBuilder;
import ru.vtb.uasp.beg.producer.BusinessEventProducer;
import ru.vtb.uasp.beg.producer.BusinessEventProducerBuilder;

import javax.annotation.Resource;
import java.util.Properties;

@Configuration
@ConditionalOnProperty(name = "beg.producer.configType", havingValue = "LOCAL")
public class LocalProducerConfig {

    @Resource
    private ProducerProperties producerProperties;

    @Resource
    private ResourceLoader resourceLoader;

    @Bean
    @SneakyThrows
    public SchemaRegistryClient schemaRegistryClient() {
        return SchemaRegistryClientBuilder.local()
                .schemaRegistryDirectory(producerProperties.getFileSchemaRegistryClient().getPath())
                .build();
    }

    @Bean
    @SneakyThrows
    public TransportManagerClient transportManagerClient() {
        LocalTransportManagerClientBuilder builder = TransportManagerClientBuilder.local();
        if (!ObjectUtils.isEmpty(producerProperties.getFileTransportStateManagerClient().getPath())) {
            builder.importFromJsonFile(producerProperties.getFileTransportStateManagerClient().getPath());
        }
        builder.add(producerProperties.getTransportStates());
        return builder.build();
    }

    @Bean
    public BusinessEventProducerBuilder businessEventProducerBuilder() {
        Properties kafkaProperties = new Properties();
        kafkaProperties.putAll(ConfigUtils.getKafkaProducerProperties(MapUtils.toProperties(producerProperties.getKafka())));
        return new BusinessEventProducerBuilder()
                .schemaRegistryClient(schemaRegistryClient())
                .transportStateManager(transportManagerClient())
                .kafkaProperties(kafkaProperties);
    }

    @Bean
    public BusinessEventProducer sampleBusinessEventProducer() {
        return businessEventProducerBuilder().build();
    }
}
