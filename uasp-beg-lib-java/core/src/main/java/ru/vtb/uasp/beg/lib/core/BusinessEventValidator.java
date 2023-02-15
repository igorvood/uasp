package ru.vtb.uasp.beg.lib.core;

public final class BusinessEventValidator {

    public static <EVENT> BusinessEvent requireAnnotation(Class<EVENT> eventClass) {
        BusinessEvent businessEventAnnotation = eventClass.getAnnotation(BusinessEvent.class);
        if (businessEventAnnotation == null)
            throw new RuntimeException("BusinessEvent annotation is not found on class " + eventClass);
        return businessEventAnnotation;
    }

}
