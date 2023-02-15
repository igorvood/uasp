package ru.vtb.uasp.beg.lib.producertest.config;

import org.apache.commons.collections.MapUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.vtb.uasp.beg.lib.core.kafka.ConfigUtils;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.SchemaRegistryClient;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.impl.SchemaRegistryClientBuilder;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportManagerClient;
import ru.vtb.uasp.beg.lib.transportManagerClient.impl.TransportManagerClientBuilder;
import ru.vtb.uasp.beg.producer.BusinessEventProducer;
import ru.vtb.uasp.beg.producer.BusinessEventProducerBuilder;

import javax.annotation.Resource;
import java.util.Properties;

@Configuration
@ConditionalOnProperty(name = "beg.producer.configType", havingValue = "REMOTE")
public class RemoteProducerConfig {

    @Resource
    private ProducerProperties producerProperties;

    @Bean
    public SchemaRegistryClient schemaRegistryClient() {
        return SchemaRegistryClientBuilder.remote()
                .url(producerProperties.getHttpSchemaRegistryClient().getUrl())
                .jwtToken(producerProperties.getHttpSchemaRegistryClient().getJwtToken())
                .build();
    }

    @Bean
    public TransportManagerClient transportManagerClient() {
        Properties kafkaProperties = new Properties();

        return TransportManagerClientBuilder.remote()
                .httpSupplierBaseUrl(producerProperties.getRemoteTransportStateManagerClient().getHttpSupplierBaseUrl())
                .httpSupplierJwtToken(producerProperties.getRemoteTransportStateManagerClient().getHttpSupplierJwtToken())
                .kafkaSupplierProperties(kafkaProperties)
                .kafkaSupplierAddress(producerProperties.getRemoteTransportStateManagerClient().getKafkaSupplierAddress())
                .kafkaSupplierTopic(producerProperties.getRemoteTransportStateManagerClient().getKafkaSupplierTopic())
                .kafkaSupplierGroup(producerProperties.getRemoteTransportStateManagerClient().getKafkaSupplierGroup())
                .build();
    }

    @Bean
    public BusinessEventProducer businessEventProducer(SchemaRegistryClient schemaRegistryClient, TransportManagerClient transportManagerClient) {
        Properties kafkaProperties = new Properties();
        kafkaProperties.putAll(ConfigUtils.getKafkaProducerProperties(MapUtils.toProperties(producerProperties.getKafka())));
        return new BusinessEventProducerBuilder()
                .schemaRegistryClient(schemaRegistryClient)
                .transportStateManager(transportManagerClient)
                .kafkaProperties(kafkaProperties)
                .build();
    }

}
