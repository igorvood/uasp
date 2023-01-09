# Apache Flink: Деплой jar через REST API

#### Зависимости:

- curl (7.68.0) - для работы с http
- jq (1.6) - для работы с json ответами

#### Настройки:

- [.env](.env) - общие настройки
- *.env - настройки для каждого сервиса

#### Скрипты:

###### Деплой разделён на две стадии:

1. Загрузка файла  - [upload-jar.sh](upload-jar.sh) файл.jar <имя_сервиса>
   `./upload-jar.sh ../../target/uasp-streaming-input-convertor-1.1.jar tsg_way4_input-convertor`
   Если не указан параметр <имя__сервиса>, предварительно не бует призведено удаление сервиса с таки имененм.
2. Запуск сервиса - [run-jar.sh](upload-jar.sh) файл.jar файл.jar имя_сервиса параллельность env__файл
   `./run-jar.sh ../../target/uasp-streaming-input-convertor-1.1.jar tsg_way4_input_convertor 4 way4.env`
   `./run-jar.sh ../../target/uasp-streaming-input-convertor-1.1.jar tsg_mdm_input_convertor 4 mdm.env`

_*.sh - вспомогательные скрипты, например


`_cancel-service-with-name.sh tsg_mdm_input_convertor` - удаление сервиса
