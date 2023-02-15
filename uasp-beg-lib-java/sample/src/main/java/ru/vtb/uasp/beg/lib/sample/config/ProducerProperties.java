package ru.vtb.uasp.beg.lib.sample.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportState;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "beg.producer")
@Getter
@Setter
public class ProducerProperties {

    private Map<String, String> kafka = new LinkedHashMap<>();

    private String configType;

    private HttpSchemaRegistryClient httpSchemaRegistryClient;
    private RemoteTransportStateManagerClient remoteTransportStateManagerClient;

    private FileSchemaRegistryClient fileSchemaRegistryClient;
    private FileTransportStateManagerClient fileTransportStateManagerClient;

    private List<TransportState> transportStates = new ArrayList<>();

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
