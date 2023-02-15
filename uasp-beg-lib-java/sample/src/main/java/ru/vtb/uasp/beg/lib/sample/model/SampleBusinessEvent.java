package ru.vtb.uasp.beg.lib.sample.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.vtb.uasp.beg.lib.core.BusinessEvent;


@BusinessEvent(name = "sample", version = "1")
@Getter
@EqualsAndHashCode
@ToString
public class SampleBusinessEvent {

    @JsonProperty("x")
    private final Integer x;

    @JsonCreator
    public SampleBusinessEvent(@JsonProperty("x") Integer x) {
        this.x = x;
    }
}