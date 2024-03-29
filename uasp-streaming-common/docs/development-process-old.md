# Процесс разработки streasming-сервисов



## Разработка streaming-сервиса

Разработка или изменение streaming-сервиса ведётся в отдельной ветке системы контроля версий отличной от develop и master. Streaming-сервис должен быть разработан таким образом, чтобы основной алгоритм преобразования был выделен в отдельную функци(и)ю, котор(ые)ую можно покрыть unit тестами и подключить как внешнюю библиотеку к интеграционному тесту. Основной алгоритм преобразования streaming-сервиса должен быть покрыт unit тестами 100%, так как интеграционный тест в дальнейшем опирается на функции преобразования как на истину в первой инстанции. 

В корне streaming-сервиса должен быть скрипт для deploy на кластер Apache Flink с именем deploy-cluster.sh для инсталляции streaming-сервиса для выполнения атомарного  интеграционного теста. Для тестирования в общем pipeline в корне должен быть скрипт deploy-cluster-itest.sh. Основное отличие скриптов друг от друга заключается использования разных топиков и имен сервисов.



## Разработка атомарного интеграционного теста для streaming-сервиса

Разработка интеграционного теста выполняется после разработки streaming-сервиса в отдельной ветке системы контроля версий отличной от develop и master. Для каждого streaming-сервиса должен быть разработан интеграционный тест, что позволяет встроить streaming-сервис в общий pipeline с минимальными усилиями. После проведения успешного интеграционного тестирования изменения переносятся в ветку develop и выполняется её push в систему контроля версий.

В будущем встраивание streaming-сервиса в тестирование общего pipeline будет выполнятся автоматически, после готовности интеграционного теста.  

В корне проекта интеграционного теста должен быть скрипт start-test.sh который запускает атомарный интеграционный тест.



## Встраивание интеграционного теста streaming-сервиса в общий тест pipeline

Разработка встраивания тестирования streaming-сервиса в общий pipeline проект uasp-streaming-common-etl-all-itest  ведётся в отдельной ветке системы контроля версий отличной от develop и master. После встраивания  вызова streaming-сервиса в  uasp-streaming-common-etl-all-itest выполняем полное тестирование всего pipeline, после успешного прохождения выполняем перенос сделанных изменений в ветку develop для uasp-streaming-common-etl-all-itest и streaming-сервиса. После обновления ветки develop, проводим окончательный тест всего pipeline, после чего можно считать задачу по изменению или разработки streaming-сервиса завершённой для чего выполняем push ветки develop для uasp-streaming-common-etl-all-itest и streaming-сервиса в систему контроля версий.

В ветку master добавляем изменения streaming-сервиса после успешного прохождения полного интеграционного теста всего pipeline с внедрёнными изменениями в него steaming-сервиса.  Изменения в ветку master переносятся сразу для 3-х проектов: uasp-streaming-common-etl-all-itest, атомарный интеграционный тест streaming-сервиса и streaming-сервис, после чего выполняется тест всего pipeline и по результатам успешного тестирование выполняется push в систему контроля версий.



