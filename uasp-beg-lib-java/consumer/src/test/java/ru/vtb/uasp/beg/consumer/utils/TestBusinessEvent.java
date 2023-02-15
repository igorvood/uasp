package ru.vtb.uasp.beg.consumer.utils;

import java.util.Objects;

public abstract class TestBusinessEvent {
    public Long id;
    public String strData;
    public Integer intData;
    public Boolean boolData;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestBusinessEvent that = (TestBusinessEvent) o;
        return Objects.equals(id, that.id)
                && Objects.equals(strData, that.strData)
                && Objects.equals(intData, that.intData)
                && Objects.equals(boolData, that.boolData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, strData, intData, boolData);
    }
}
