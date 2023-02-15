package ru.vtb.uasp.beg.lib.producertest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.vtb.uasp.beg.lib.core.BusinessEvent;

/**
 * A Second business event v1
 */
@BusinessEvent(name = "second", version = "1")
@Data
@Builder
@AllArgsConstructor
public class SecondBusinessEvent {
    private long id;
    private String name;
    private Integer intValue;
    private Double doubleValue;
    private Byte byteValue;
    private long timestamp;
}
