package ru.vtb.uasp.beg.producer.utils;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import ru.vtb.uasp.beg.lib.core.BusinessEvent;

/**
 * A simple business event #2
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"id", "x", "y"})
@BusinessEvent(name = SimpleBusinessEvent2.NAME, version = SimpleBusinessEvent2.VERSION)
@Getter
public class SimpleBusinessEvent2 {
    public static final String NAME = "SimpleBusinessEvent_2";
    public static final String VERSION = "1";

    /**
     * Id (required)
     */
    @JsonProperty("id")
    @JsonPropertyDescription("Id")
    private final int id;

    /**
     * Some numeric value (required)
     */
    @JsonProperty("x")
    @JsonPropertyDescription("Some numeric value")
    private final Integer x;

    /**
     * Some string value
     */
    @JsonProperty("y")
    @JsonPropertyDescription("Some string value")
    private final String y;

    @JsonCreator
    public SimpleBusinessEvent2(@JsonProperty("id") int id, @JsonProperty("x") Integer x, @JsonProperty("y") String y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
}
