package ru.vtb.uasp.beg.lib.producertest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.vtb.uasp.beg.lib.core.BusinessEvent;
import ru.vtb.uasp.beg.lib.producertest.model.SimpleBusinessEvent2;
import ru.vtb.uasp.beg.lib.producertest.model.SimpleBusinessEvent3;
import ru.vtb.uasp.beg.lib.producertest.model.SimpleBusinessEvent4;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.ConfluentSchema;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.SchemaRegistryClient;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportManagerClient;
import ru.vtb.uasp.beg.lib.transportManagerClient.TransportState;
import ru.vtb.uasp.beg.producer.BusinessEventProducer;
import ru.vtb.uasp.beg.producer.validator.SchemaValidationException;
import ru.vtb.uasp.beg.producer.validator.SchemaValidator;

import javax.annotation.Resource;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import static ru.vtb.uasp.beg.lib.core.BusinessEventValidator.requireAnnotation;

@RestController
@Profile("mvc")
public class TestController {

    private final static ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static int nextId;

    private enum TestCase {
        TC1("1. Передача БС SimpleBusinessEvent2, соответствующего схеме (заполнены все поля) в kafka при включенной публикации этого БС", () -> createEvent2()),
        TC2("2. Передача БС SimpleBusinessEvent4, соответствующего схеме в kafka при включенной публикации этого БС, топик не существует", () -> createEvent4()),
        TC3("3. Передача БС SimpleBusinessEvent3, соответствующего схеме в kafka при выключенной публикации этого БС", () -> createEvent3()),
        TC4("4. Передача БС SimpleBusinessEvent2, соответствующего схеме (заполнены только обязательные поля) в kafka при включенной публикации этого БС", () -> createEvent2Req()),
        TC5("5. Передача БС SimpleBusinessEvent3, соответствующего схеме (заполнены только обязательные поля) в kafka при выключенной публикации этого БС", () -> createEvent3Req()),
        TC6("6. Передача БС SimpleBusinessEvent2, не соответствующего схеме в kafka", () -> createEventWrong());

        private final String name;
        private final Supplier<Object> eventSupplier;

        TestCase(String name, Supplier<Object> eventSupplier) {
            this.name = name;
            this.eventSupplier = eventSupplier;
        }
    }

    ;
    private final SchemaValidator schemaValidator = new SchemaValidator();

    @Resource
    private BusinessEventProducer businessEventProducer;
    @Resource
    private TransportManagerClient transportManagerClient;
    @Resource
    private SchemaRegistryClient schemaRegistryClient;

    @GetMapping("/")
    public String getTests() {
        String result = "<P><TABLE>";
        for (int i = 0; i < TestCase.values().length; i++) {
            TestCase testCase = TestCase.values()[i];
            result += "<TR><TD><A HREF='/test/" + i + "'>" + testCase.name + "</A></TD></TR>";
        }
        result += "</TABLE>";
        return result;
    }

    @GetMapping("/test/{id}")
    public String performTest(@PathVariable int id) {
        if (id >= 0 && id < TestCase.values().length) {
            TestCase testCase = TestCase.values()[id];
            Object event = testCase.eventSupplier.get();
            try {
                businessEventProducer.send(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return createResponse(testCase.name, event);
        } else {
            throw new IllegalStateException("Unsupported test id");
        }
    }

    // Создание БС, соответствующего схеме (заполенны все поля)
    static SimpleBusinessEvent2 createEvent2() {
        return new SimpleBusinessEvent2(++nextId, ThreadLocalRandom.current().nextInt(), SimpleBusinessEvent2.class.getSimpleName(), "2019-07-04T13:33:03.969Z");
    }

    // Создание БС, соответствующего схеме (заполенны только обязательные поля)
    static SimpleBusinessEvent2Req createEvent2Req() {
        return new SimpleBusinessEvent2Req(++nextId, ThreadLocalRandom.current().nextInt());
    }

    // Создание БС, не соответствующего схеме (не все обязательные поля присутствуют)
    static SimpleBusinessEvent2Wrong createEventWrong() {
        return new SimpleBusinessEvent2Wrong(++nextId, String.valueOf(ThreadLocalRandom.current().nextInt()));
    }

    // Создание БС SimpleBusinessEvent3, соответствующего схеме
    static SimpleBusinessEvent3 createEvent3() {
        return new SimpleBusinessEvent3(++nextId, ThreadLocalRandom.current().nextInt(), SimpleBusinessEvent3.class.getSimpleName());
    }

    // Создание БС SimpleBusinessEvent3, соответствующего схеме
    static SimpleBusinessEvent3Req createEvent3Req() {
        return new SimpleBusinessEvent3Req(++nextId);
    }

    // Создание БС SimpleBusinessEvent4, соответствующего схеме
    static SimpleBusinessEvent4 createEvent4() {
        return new SimpleBusinessEvent4(++nextId, ThreadLocalRandom.current().nextInt(), SimpleBusinessEvent4.class.getSimpleName());
    }

    private String createResponse(String testCase, Object event) {
        try {
            BusinessEvent annotation = requireAnnotation(event.getClass());
            String json = serialize(event);
            ConfluentSchema schema = schemaRegistryClient.getSchema(annotation.name(), annotation.version()).block();
            boolean isSchemaValid = true;
            try {
                schemaValidator.validate(json, schema);
            } catch (SchemaValidationException e) {
                isSchemaValid = false;
            }
            TransportState state = transportManagerClient.getTransportState(annotation.name()).block();
            String strValid = isSchemaValid ? "<SPAN style=\"color:green\">Соответствует</SPAN>" : "<SPAN style=\"color:red\">Не соответствует</SPAN>";
            String strTS = state.isEnabled() ? "<SPAN style=\"color:green\">Публикация включена</SPAN>" : "<SPAN style=\"color:red\">Публикация выключена</SPAN>";
            return "<P><B>" + testCase + "</B>" +
                    "<TABLE>" +
                    "<TR><TD>БС:</TD><TD>" + json + "</TD><TD></TD></TR>\n" +
                    "<TR><TD>Схема:</TD><TD>" + schema.getSchema() + "</TD><TD>" + strValid + "</TD></TR>\n" +
                    "<TR><TD>Состояние:</TD><TD>" + state + "</TD><TD>" + strTS + "</TD></TR>\n" +
                    "</TABLE>";
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private String serialize(Object businessEvent) {
        try {
            return MAPPER.writeValueAsString(businessEvent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * SimpleBusinessEvent2, содержащий только обязательне поля
     */
    @BusinessEvent(name = "SimpleBusinessEvent_2", version = "1")
    @Data
    @AllArgsConstructor
    @Builder
    static class SimpleBusinessEvent2Req {
        private int id;
        private int x;
    }

    /**
     * SimpleBusinessEvent3, содержащий только обязательне поля
     */
    @BusinessEvent(name = "SimpleBusinessEvent_3", version = "1")
    @Data
    @AllArgsConstructor
    @Builder
    static class SimpleBusinessEvent3Req {
        private int v;
    }

    /**
     * SimpleBusinessEvent2, несоответствующий схеме
     */
    @BusinessEvent(name = "SimpleBusinessEvent_2", version = "1")
    @Data
    @AllArgsConstructor
    @Builder
    static class SimpleBusinessEvent2Wrong {
        private int id;
        private String y;
    }

}
