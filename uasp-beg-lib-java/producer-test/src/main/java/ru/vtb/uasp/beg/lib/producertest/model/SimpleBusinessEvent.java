package ru.vtb.uasp.beg.lib.producertest.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Builder;
import lombok.Data;
import ru.vtb.uasp.beg.lib.core.BusinessEvent;

import java.util.Objects;


/**
 * SimpleBusinessEvent_1
 * <p>
 * A simple business event schema
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"x", "ts"})
@BusinessEvent(name = "SimpleBusinessEvent_1", version = "1")
@Data
@Builder
public class SimpleBusinessEvent {

    /**
     * Just numeric value
     * (Required)
     */
    @JsonProperty("x")
    @JsonPropertyDescription("Just numeric value")
    private final int x;

    @JsonProperty("ts")
    @JsonPropertyDescription("Timestamp")
    private final long ts;

    @JsonCreator
    public SimpleBusinessEvent(@JsonProperty("x") int x, @JsonProperty("ts") long ts) {
        this.x = x;
        this.ts = ts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleBusinessEvent that = (SimpleBusinessEvent) o;
        return x == that.x && ts == that.ts;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, ts);
    }

    @Override
    public String toString() {
        return "SampleBusinessEvent{" +
                "x=" + x +
                ", ts=" + ts +
                '}';
    }
}
