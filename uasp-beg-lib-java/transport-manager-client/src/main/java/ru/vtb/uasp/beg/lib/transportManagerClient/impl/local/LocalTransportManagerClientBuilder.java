package ru.vtb.uasp.beg.lib.transportManagerClient.impl.local;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.vtb.uasp.beg.lib.core.utils.ResourceUtils;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportState;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalTransportManagerClientBuilder {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final TypeReference<List<TransportState>> TSL_TYPE = new TypeReference<List<TransportState>>() {
    };

    private final Map<String, TransportState> transportStates = new HashMap<>();

    /**
     * Imports transport states from a file in JSON format
     *
     * @param transportStatesDescriptionFile file to parse
     * @return
     */
    public LocalTransportManagerClientBuilder importFromJsonFile(String transportStatesDescriptionFile) {
        String json = ResourceUtils.getResourceAsString(transportStatesDescriptionFile);
        importFromJson(json);
        return this;
    }

    /**
     * Imports transport states from JSON, which includes an array of TransportState entries, i.e.
     * <pre>
     * [
     *   {
     *     "name": "sample",
     *     "topicName": "sample_event_topic",
     *     "kafkaAddress": "localhost:29092",
     *     "lastUpdateTimestamp": 0,
     *     "enabled": true
     *   },
     *   ...
     * ]
     * </pre>
     *
     * @param json json string to parse
     * @return
     */
    public LocalTransportManagerClientBuilder importFromJson(String json) {
        try {
            List<TransportState> states = MAPPER.readValue(json, TSL_TYPE);
            states.forEach(s -> transportStates.put(s.getName(), s));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Adds a TransportState entry
     *
     * @param entry an entry
     * @return
     */
    public LocalTransportManagerClientBuilder add(TransportState entry) {
        transportStates.put(entry.getName(), entry);
        return this;
    }

    /**
     * Adds a collection of TransportState entries
     *
     * @param entries collection of entries
     * @return
     */
    public LocalTransportManagerClientBuilder add(Collection<TransportState> entries) {
        entries.forEach(s -> transportStates.put(s.getName(), s));
        return this;
    }

    public LocalTransportManagerClient build() {
        return new LocalTransportManagerClient(transportStates::get);
    }
}
