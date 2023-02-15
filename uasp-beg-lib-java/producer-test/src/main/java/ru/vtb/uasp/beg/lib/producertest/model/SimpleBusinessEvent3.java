package ru.vtb.uasp.beg.lib.producertest.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.vtb.uasp.beg.lib.core.BusinessEvent;


/**
 * SimpleBusinessEvent_3
 * <p>
 * A simple business event 3 schema
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@BusinessEvent(name = "SimpleBusinessEvent_3", version = "1")
@AllArgsConstructor
@Builder
@Data
public class SimpleBusinessEvent3 {

    @JsonProperty("id")
    @JsonPropertyDescription("Identifier")
    private final int id;

    @JsonProperty("v")
    @JsonPropertyDescription("Just numeric value")
    private final int v;

    @JsonProperty("s")
    @JsonPropertyDescription("Some string value")
    private final String s;

}
