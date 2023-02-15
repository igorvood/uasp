package ru.vtb.uasp.beg.producer.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.vtb.uasp.beg.lib.core.BusinessEvent;

/**
 * A complex business event including a lot of fields
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@BusinessEvent(name = ComplexBusinessEvent.NAME, version = ComplexBusinessEvent.VERSION)
@Getter
@AllArgsConstructor
@Builder
public class ComplexBusinessEvent {
    public static final String NAME = "ComplexBusinessEvent";
    public static final String VERSION = "1";

    private final boolean booleanField1;
    private final boolean booleanField2;
    private final boolean booleanField3;
    private final boolean booleanField4;
    private final boolean booleanField5;
    private final boolean booleanField6;
    private final boolean booleanField7;
    private final boolean booleanField8;
    private final boolean booleanField9;
    private final boolean booleanField10;

    private final byte byteField1;
    private final byte byteField2;
    private final byte byteField3;
    private final byte byteField4;
    private final byte byteField5;
    private final byte byteField6;
    private final byte byteField7;
    private final byte byteField8;
    private final byte byteField9;
    private final byte byteField10;

    private final Character charField1;
    private final Character charField2;
    private final Character charField3;
    private final Character charField4;
    private final Character charField5;
    private final Character charField6;
    private final Character charField7;
    private final Character charField8;
    private final Character charField9;
    private final Character charField10;

    private final Double doubleField1;
    private final Double doubleField2;
    private final Double doubleField3;
    private final Double doubleField4;
    private final Double doubleField5;
    private final Double doubleField6;
    private final Double doubleField7;
    private final Double doubleField8;
    private final Double doubleField9;
    private final Double doubleField10;

    private final Float floatField1;
    private final Float floatField2;
    private final Float floatField3;
    private final Float floatField4;
    private final Float floatField5;
    private final Float floatField6;
    private final Float floatField7;
    private final Float floatField8;
    private final Float floatField9;
    private final Float floatField10;

    private final Integer intField1;
    private final Integer intField2;
    private final Integer intField3;
    private final Integer intField4;
    private final Integer intField5;
    private final Integer intField6;
    private final Integer intField7;
    private final Integer intField8;
    private final Integer intField9;
    private final Integer intField10;

    private final Long longField1;
    private final Long longField2;
    private final Long longField3;
    private final Long longField4;
    private final Long longField5;
    private final Long longField6;
    private final Long longField7;
    private final Long longField8;
    private final Long longField9;
    private final Long longField10;

    private final Short shortField1;
    private final Short shortField2;
    private final Short shortField3;
    private final Short shortField4;
    private final Short shortField5;
    private final Short shortField6;
    private final Short shortField7;
    private final Short shortField8;
    private final Short shortField9;
    private final Short shortField10;
}
