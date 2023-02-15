package ru.vtb.uasp.beg.lib.transportManagerClient.impl.remote.suppliers.kafka;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportState;

import java.io.IOException;

public class JsonTransportStateDeserializer implements Deserializer<TransportState> {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public TransportState deserialize(String topic, byte[] data) {
        try {
            return MAPPER.readValue(data, TransportState.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
