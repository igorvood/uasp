package ru.vtb.uasp.beg.producer;

import ru.vtb.uasp.beg.lib.schemaRegistryClient.SchemaRegistryClient;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportManagerClient;

import java.util.Properties;
import java.util.function.Consumer;

public class BusinessEventProducerBuilder {
    private TransportManagerClient transportStateManager;
    private SchemaRegistryClient schemaRegistryClient;
    private Properties kafkaProperties;
    private Consumer<ProducerResult> producerResultConsumer;
    private Consumer<Throwable> errorConsumer;

    public BusinessEventProducerBuilder transportStateManager(TransportManagerClient transportStateManager) {
        this.transportStateManager = transportStateManager;
        return this;
    }

    public BusinessEventProducerBuilder schemaRegistryClient(SchemaRegistryClient schemaRegistryClient) {
        this.schemaRegistryClient = schemaRegistryClient;
        return this;
    }

    public BusinessEventProducerBuilder kafkaProperties(Properties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
        return this;
    }

    public BusinessEventProducerBuilder sendResultConsumer(Consumer<ProducerResult> producerResultConsumer) {
        this.producerResultConsumer = producerResultConsumer;
        return this;
    }

    public BusinessEventProducerBuilder errorConsumer(Consumer<Throwable> errorConsumer) {
        this.errorConsumer = errorConsumer;
        return this;
    }

    public BusinessEventProducer build() {
        return new BusinessEventProducer(
                transportStateManager,
                schemaRegistryClient,
                kafkaProperties,
                producerResultConsumer,
                errorConsumer);
    }
}
