package ru.vtb.uasp.beg.lib.schemaRegistryClient.impl;

import ru.vtb.uasp.beg.lib.schemaRegistryClient.SchemaRegistryClient;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.impl.suppliers.LocalSchemaSupplier;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.impl.suppliers.RemoteSchemaSupplier;

import java.time.Duration;

public class SchemaRegistryClientBuilder {

    public static LocalSchemaRegistryClientBuilder local() {
        return new LocalSchemaRegistryClientBuilder();
    }

    public static RemoteSchemaRegistryClientBuilder remote() {
        return new RemoteSchemaRegistryClientBuilder();
    }

    public static class RemoteSchemaRegistryClientBuilder {
        private String url;
        private String jwtToken;
        private Duration responseTimeout = Duration.ofSeconds(10);

        public RemoteSchemaRegistryClientBuilder url(String url) {
            this.url = url;
            return this;
        }

        public RemoteSchemaRegistryClientBuilder jwtToken(String jwtToken) {
            this.jwtToken = jwtToken;
            return this;
        }

        public RemoteSchemaRegistryClientBuilder responseTimeout(Duration responseTimeout) {
            this.responseTimeout = responseTimeout;
            return this;
        }

        public SchemaRegistryClient build() {
            return new SchemaRegistryClientImpl(new RemoteSchemaSupplier(url, jwtToken, responseTimeout));
        }
    }

    public static class LocalSchemaRegistryClientBuilder {
        private String schemaRegistryDirectoryPath;

        public LocalSchemaRegistryClientBuilder schemaRegistryDirectory(String schemaRegistryDirectoryPath) {
            this.schemaRegistryDirectoryPath = schemaRegistryDirectoryPath;
            return this;
        }

        public SchemaRegistryClient build() {
            return new SchemaRegistryClientImpl(new LocalSchemaSupplier(schemaRegistryDirectoryPath));
        }
    }
}
