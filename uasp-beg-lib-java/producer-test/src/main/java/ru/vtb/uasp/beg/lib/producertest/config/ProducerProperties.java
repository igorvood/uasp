package ru.vtb.uasp.beg.lib.producertest.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportState;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "beg.producer")
@Getter
@Setter
public class ProducerProperties {

    // Kafka producer properties
    private Map<String, String> kafka = new LinkedHashMap<>();

    // Transport states
    private List<TransportState> transportStates = new ArrayList<>();

    // Producer settings
    private int batches = 1;
    private int batchSize = 1000;
    private Duration batchInterval = Duration.of(10, ChronoUnit.SECONDS);
    private boolean randomEvent = true;
    private boolean randomBatchSize = true;

    private HttpSchemaRegistryClient httpSchemaRegistryClient;
    private RemoteTransportStateManagerClient remoteTransportStateManagerClient;

    private FileSchemaRegistryClient fileSchemaRegistryClient;
    private FileTransportStateManagerClient fileTransportStateManagerClient;

    //--------------------------------
    @Getter
    @Setter
    public static class HttpSchemaRegistryClient {
        private String url;
        private String jwtToken;
    }

    @Getter
    @Setter
    public static class RemoteTransportStateManagerClient {
        private String httpSupplierBaseUrl;
        private String httpSupplierJwtToken;
        private String kafkaSupplierAddress;
        private String kafkaSupplierTopic;
        private String kafkaSupplierGroup;
    }

    @Getter
    @Setter
    public static class FileSchemaRegistryClient {
        private String path;
    }

    @Getter
    @Setter
    public static class FileTransportStateManagerClient {
        private String path;
    }
}
