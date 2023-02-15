package ru.vtb.uasp.beg.producer.utils;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import ru.vtb.uasp.beg.lib.core.BusinessEvent;

/**
 * A simple business event #1
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"x"})
@BusinessEvent(name = SimpleBusinessEvent1.NAME, version = SimpleBusinessEvent1.VERSION)
@Getter
public class SimpleBusinessEvent1 {
    public static final String NAME = "SimpleBusinessEvent_1";
    public static final String VERSION = "1";

    /**
     * Some numeric value (required)
     */
    @JsonProperty("x")
    @JsonPropertyDescription("Some numeric value")
    private final Integer x;

    @JsonCreator
    public SimpleBusinessEvent1(@JsonProperty("x") Integer x) {
        this.x = x;
    }
}
