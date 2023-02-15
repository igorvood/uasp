package ru.vtb.uasp.beg.producer.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Getter;
import ru.vtb.uasp.beg.lib.core.BusinessEvent;

/**
 * A simple bad business event with non-existing schema
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@BusinessEvent(name = "BadBusinessEvent", version = "1")
@Getter
public class BadBusinessEvent {

    /**
     * Some data
     */
    @JsonProperty("data")
    @JsonPropertyDescription("Some date")
    private final String data;

    @JsonCreator
    public BadBusinessEvent(@JsonProperty("data") String data) {
        this.data = data;
    }
}
