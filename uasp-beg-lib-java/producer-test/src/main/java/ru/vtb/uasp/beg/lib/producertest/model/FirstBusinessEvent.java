package ru.vtb.uasp.beg.lib.producertest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.vtb.uasp.beg.lib.core.BusinessEvent;

/**
 * A First business event v1
 */
@BusinessEvent(name = "first", version = "1")
@Data
@Builder
@AllArgsConstructor
public class FirstBusinessEvent {
    private long id;
    private String name;
    private String description;
    private long timestamp;
}
