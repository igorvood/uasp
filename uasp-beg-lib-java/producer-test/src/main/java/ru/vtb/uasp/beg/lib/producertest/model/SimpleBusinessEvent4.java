package ru.vtb.uasp.beg.lib.producertest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.vtb.uasp.beg.lib.core.BusinessEvent;


/**
 * SimpleBusinessEvent_4
 * <p>
 * A simple business event 4 schema
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@BusinessEvent(name = "SimpleBusinessEvent_4", version = "1")
@AllArgsConstructor
@Builder
@Data
public class SimpleBusinessEvent4 {

    @JsonProperty("id")
    @JsonPropertyDescription("Identifier")
    private final int id;

    @JsonProperty("x")
    @JsonPropertyDescription("Just numeric value")
    private final int x;

    @JsonProperty("y")
    @JsonPropertyDescription("Some string value")
    private final String y;

}
