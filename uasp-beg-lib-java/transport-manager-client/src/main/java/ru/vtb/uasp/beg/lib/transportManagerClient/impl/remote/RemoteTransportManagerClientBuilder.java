package ru.vtb.uasp.beg.lib.transportManagerClient.impl.remote;

import ru.vtb.uasp.beg.lib.transportManagerClient.TransportManagerClient;
import ru.vtb.uasp.beg.lib.transportManagerClient.impl.remote.suppliers.http.HttpTransportStateSupplier;
import ru.vtb.uasp.beg.lib.transportManagerClient.impl.remote.suppliers.kafka.KafkaTransportStateSupplier;

import java.time.Duration;
import java.util.Properties;

public class RemoteTransportManagerClientBuilder {

    private String httpSupplierBaseUrl;
    private String httpSupplierJwtToken;
    private Duration httpSupplierResponseTimeout = Duration.ofSeconds(10);
    private Properties kafkaSupplierProperties;
    private String kafkaSupplierAddress;
    private String kafkaSupplierTopic;
    private String kafkaSupplierGroup;

    public RemoteTransportManagerClientBuilder httpSupplierBaseUrl(String httpSupplierBaseUrl) {
        this.httpSupplierBaseUrl = httpSupplierBaseUrl;
        return this;
    }

    public RemoteTransportManagerClientBuilder httpSupplierJwtToken(String httpSupplierJwtToken) {
        this.httpSupplierJwtToken = httpSupplierJwtToken;
        return this;
    }

    public RemoteTransportManagerClientBuilder httpSupplierResponseTimeout(Duration httpSupplierResponseTimeout) {
        this.httpSupplierResponseTimeout = httpSupplierResponseTimeout;
        return this;
    }

    public RemoteTransportManagerClientBuilder kafkaSupplierProperties(Properties kafkaSupplierProperties) {
        this.kafkaSupplierProperties = kafkaSupplierProperties;
        return this;
    }

    public RemoteTransportManagerClientBuilder kafkaSupplierAddress(String kafkaSupplierAddress) {
        this.kafkaSupplierAddress = kafkaSupplierAddress;
        return this;
    }

    public RemoteTransportManagerClientBuilder kafkaSupplierTopic(String kafkaSupplierTopic) {
        this.kafkaSupplierTopic = kafkaSupplierTopic;
        return this;
    }

    public RemoteTransportManagerClientBuilder kafkaSupplierGroup(String kafkaSupplierGroup) {
        this.kafkaSupplierGroup = kafkaSupplierGroup;
        return this;
    }

    public TransportManagerClient build() {
        HttpTransportStateSupplier httpTransportStateSupplier = new HttpTransportStateSupplier(httpSupplierBaseUrl, httpSupplierJwtToken, httpSupplierResponseTimeout);
        KafkaTransportStateSupplier kafkaTransportStateSupplier = new KafkaTransportStateSupplier(kafkaSupplierProperties, kafkaSupplierAddress, kafkaSupplierGroup, kafkaSupplierTopic);
        return new RemoteTransportManagerClient(httpTransportStateSupplier, kafkaTransportStateSupplier);
    }
}
