package ru.vtb.uasp.beg.producer.validator;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.ConfluentSchema;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.SchemaRegistryClient;
import ru.vtb.uasp.beg.lib.schemaRegistryClient.impl.SchemaRegistryClientBuilder;
import ru.vtb.uasp.beg.producer.utils.SimpleBusinessEvent1;
import ru.vtb.uasp.beg.producer.utils.SimpleBusinessEvent2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.spy;

class SchemaValidatorTest {
    private static SchemaRegistryClient schemaRegistryClient;
    private static SchemaValidator schemaValidator;
    private static final Map<String, ConfluentSchema> SCHEMAS = new HashMap<>();

    @BeforeAll
    @SneakyThrows
    static void setUp() {
        schemaRegistryClient = spy(SchemaRegistryClientBuilder.local()
                .schemaRegistryDirectory("classpath:schemaRegistry")
                .build());
        schemaValidator = new SchemaValidator();
        Collection<Mono<ConfluentSchema>> schemas = Stream.of(Tuples.of(SimpleBusinessEvent1.NAME, SimpleBusinessEvent1.VERSION),
                        Tuples.of(SimpleBusinessEvent2.NAME, SimpleBusinessEvent2.VERSION))
                .map(event -> schemaRegistryClient.getSchema(event.getT1(), event.getT2()))
                .collect(Collectors.toList());
        Flux.zip(schemas, SchemaValidatorTest::<ConfluentSchema>combineToList)
                .doOnNext(list -> list.forEach(s -> SCHEMAS.put(s.getSubject() + "_" + s.getVersion(), s)))
                .blockLast();
    }

    @ParameterizedTest(name = "{1}-{2}. {0}")
    @MethodSource("validateTestArgumentsProvider")
    void validateTest(String description, String eventName, String eventVersion, String eventInJson, boolean expectedValidResult) {
        String schemaName = eventName + "_" + eventVersion;
        ConfluentSchema confluentSchema = SCHEMAS.get(schemaName);
        boolean actualValidResult = true;
        try {
            schemaValidator.validate(eventInJson, confluentSchema);
        } catch (SchemaValidationException e) {
            actualValidResult = false;
        }
        assertEquals(expectedValidResult, actualValidResult);
    }

    static Stream<Arguments> validateTestArgumentsProvider() {
        return Stream.of(
                Arguments.of("Схема некорректна: Пустые данные",
                        SimpleBusinessEvent1.NAME, SimpleBusinessEvent1.VERSION, "", false),
                Arguments.of("Схема некорректна: неправильный формат",
                        SimpleBusinessEvent1.NAME, SimpleBusinessEvent1.VERSION, "<xml><x>1</x></xml>", false),
                Arguments.of("Схема некорректна: Пустой JSON",
                        SimpleBusinessEvent1.NAME, SimpleBusinessEvent1.VERSION, "{}", false),
                Arguments.of("Схема некорректна: Отсутствует обязательное значение (x)",
                        SimpleBusinessEvent1.NAME, SimpleBusinessEvent1.VERSION, "{'y':123}", false),
                Arguments.of("Схема некорректна: Значение null не разрешено",
                        SimpleBusinessEvent1.NAME, SimpleBusinessEvent1.VERSION, "{'x':null}", false),
                Arguments.of("Схема некорректна: Неправильный формат значения",
                        SimpleBusinessEvent1.NAME, SimpleBusinessEvent1.VERSION, "{'x': 'abc'}", false),
                Arguments.of("Схема некорректна: Неправильный формат значения",
                        SimpleBusinessEvent2.NAME, SimpleBusinessEvent2.VERSION, "{'x': '123', 'y':'something'}", false),
                Arguments.of("Схема некорректна: Отсутствует обязательное значение (id)",
                        SimpleBusinessEvent2.NAME, SimpleBusinessEvent2.VERSION, "{'y':1}", false),
                Arguments.of("Схема корректна. Неизвестные поля проигнорированы",
                        SimpleBusinessEvent1.NAME, SimpleBusinessEvent1.VERSION, "{'x':123, y:'0', z:4.56}", true),
                Arguments.of("Схема корректна",
                        SimpleBusinessEvent2.NAME, SimpleBusinessEvent2.VERSION, "{'id':1, 'x':123, 'y':'something'}", true),
                Arguments.of("Схема корректна если отсутствует необязательное поле (y)",
                        SimpleBusinessEvent2.NAME, SimpleBusinessEvent2.VERSION, "{'id':1, 'x':123}", true),
                Arguments.of("Схема корректна если значение null разрешено (y)",
                        SimpleBusinessEvent2.NAME, SimpleBusinessEvent2.VERSION, "{'id':1, 'x':1, 'y':null}", true)
        );
    }

    static <T> List<T> combineToList(Object... items) {
        return Arrays.stream(items)
                .map(t -> (T) t)
                .collect(Collectors.toList());
    }


}