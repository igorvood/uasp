# uasp-streaming-input-convertor

### Streaming-cервис конвертации сообщений из множества источников

Проверка входящего сообщения на валидность json схемы и конвертация в avro UaspDto. Сообщения, которые не прошли проверку, будут направлены в dlq очередь `${app.dlq.topic.name}` с указанимем причны.

В данный момент поддерживаются следующие источники (параметр `app.uaspdto.type`):

- way4 - источник  way4 issuing-operation
- mdm - источник Кросс-ссылки 

##### Исходные данные:

* На одном уровне с проектом должен находится проект с общими зависимостями `uasp-streaming-common`.  Предварительно должны быть собраны внешние зависимости в файл `../uasp-streaming-inputconvertor-common/target/uasp-streaming-inputconvertor-common-1.0.jar`, собранный с ветки develop.

  

* Загружена дефолтная json схема в топик `${app.json.schema.topic.name}` с ключём `${app.schema.name}-${app.schema.start.version}`.  Наличие json схемы для проверки обязаетльно. Предполагается, что схема может динамически меняться и ключ схемы может приходить из:

  1. "Конверта" json сообщения, как в случае way4. Ключ схемы содержится в поле `${app.message.jsonschema.field}`, само сообщение содержится как текст в `${app.message.json.path}`.

  2. В заголовке (header) kafka сообщения с ключем schemaKey. 

  3. При отсутсвии schemaKey в заголовке, будет использоваться статическая дефолтная схема для проверки.

     

* Параметры по-умолчанию могут быть заданы в файле resources/application.properties и будут перезаписаны параметрами из командной строки

##### Пример запуска flink сценария для way4: 
```bash
uasp-streaming-input-convertor-1.0.jar \
`# Параметры для отладки ` \
`# Записывать данные в приёмник, n- выводить только на экран ` \
--input-convertor.write.to.sink y \
`# Читать источник всегда с начала` \
--input-convertor.read.source.topic.frombeginning n \
\
`# Тип источника` \
--input-convertor.uaspdto.type way4 \
`# Имя сервиса` \
--input-convertor.service.name my_way4_convertor  
`# Путь для текстового сообщения `
--input-convertor.message.json.path payload \
`# Тип сообщения` \
--input-convertor.message.jsonschema.field contentType
`# Имя входящего топика для json сообщений` \
--input-convertor.input.topic.name dev_ivruasp_realtimeway4_issuing_operationjson_one \
`# Имя исходящего топика для uaspdto сообщений` \
--input-convertor.output.topic.name dev_ivruasp_realtimeway4_issuing_operationuaspdto_one  \
`# Имя топика для невалидных сообщений сообщений (dead letter queue)` \
--input-convertor.dlq.topic.name dev_ivruasp_realtimeway4_issuing_operation__dlq_one  \
`# Имя топика с json схемой для входящего сообщения` \
--input-convertor.json.schema.topic.name streaming_json-schemas  \
`# Консьюмер группа для топика схем`\
--input-convertor.json.schema.topic.group.id streaming_json-schemas-group \
`# Консьюмер группа для входного топика `\
--input-convertor.input.topic.group.id ivr__uasp_realtime__input_converter__mdm_cross_link__json-group \
`# Тип сообщения default` \
--input-convertor.schema.name issuing-operation   \
`# Начальная версия типа сообщения default` \
--input-convertor.schema.start.version 2.1  \
`# Брокеры кафки` \
--bootstrap.servers d5uasp-apc002lk.corp.dev.vtb:9092,d5uasp-apc003lk.corp.dev.vtb:9092,d5uasp-apc004lk.corp.dev.vtb:9092 \
`# Креды для подключения к кафке `\
--input-convertor-sys.security.protocol SSL \
--input-convertor-sys.enable.auto.commit true \
--input-convertor-sys.ssl.truststore.location C:\repo\vtb-projects\vtb-cloud\ansible-kafka\tls\certs\kafka-trust.pfx \
--input-convertor-sys.ssl.truststore.password ****  \
--input-convertor-sys.ssl.keystore.location C:\repo\vtb-projects\vtb-cloud\ansible-kafka\tls\private\APD00.13.01-USBP-kafka-cluster-uasp.pfx \
--input-convertor-sys.ssl.keystore.password **** \
--input-convertor-sys.ssl.key.password ****
```


Примеры запуска для различных сред можно увидеть [conf-deployment/stages](conf-deployment/stages)

