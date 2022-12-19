# uasp-streaming-common

Общие библиотеки и документы streaming-сервисов универсальной стрименговой платформы

[Описание архитектуры](docs\architecture.md)


## Содержимое 
### Файлы:
jsonWithFields - Json Файл с набором правил.

### Классы:
package base:
1) BaseService - Трейт для обобщение сервисов. Содержит метод для расчёта бизнес логики.
2) Service - Трейт для обобщение сервисов. Содержит метод для расчёта бизнес логики.
3) OutSideSinkService - Трейт для обобщение сервисов. Содержит метод для выходного топика.
4) SinkService - Трейт для обобщение сервисов. Содержит метод для выходного топика.

package constants:
1) BigDecimalConst - Константы использующиеся при работе с BigDecimal в avro файлах. 
2) DefaultValuesConst - Дефолтные значения для различных типов файлов.

package dto:
1) WrappedObject - Объекты объёртка
2) UaspDto - Объект UaspDto

package extension:
1) CommonExtension - CommonExtension

package factory:
1) DataStreamSinkFactory - DataStreamSinkFactory$

package resource:
1) ResourceUtil - Вспомогательные утилыты для работы с ресурсами

package serialize:
1) Serializer - Сериализатор для получения ByteArray

package service:
1) ConvertInMapService - Сервис для  конвенции array в uasp, используется во многих сервисах

package state.mock:
1) MapStateMock - Мок для MapState
2) ValueStateMock - Мок для ValueState

package kafka:
1) ConsumerFactory- Создаёт kafka consumer
2) ProviderFactory - Cоздаёт kafka producer
3) DecoderFactory - Декодер для UaspDto
4) EncodeFactory - Энкодер для UaspDto
5) FlinkKafkaSerializationSchemaUasp - Сериализатор для UaspDto

package utils:
1) ConfigUtils - Набор утилит для работы с конфигами
2) JsonConverter - Набор утилит для работы с json(требует дополнения)
3) JsonUtil - Утилиты для работы с "jsonWithFields"
4) AvroSchemaUtil- Утилита для получения Avro Schema
5) AvroDeserializeUtil - Декодер для UaspDto
6) AvroSerializeUtil - Энкодер для UaspDto
