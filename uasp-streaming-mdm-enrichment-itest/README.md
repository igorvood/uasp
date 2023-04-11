# uasp-streaming-mdm-enrichment-itest

Интеграционный тест для проекта uasp-streaming-mdm-enrichment обогащения глобальным идентификатором клиента событий way4 

##### Задача интеграционного теста

Задача теста проверить процесс обогащения

##### Обязательные условия для правильной работы интеграционного теста

Обязательными условиями для правильной работы интеграционного теста является 100% покрытие логики обогащения в сервисе uasp-streaming-mdm-enrichment 

##### Описание алгорима интеграционного теста

#### Автоматизация интеграции с TestIT:

1. В некусусе должен лежать джарник конвертора отчетов
   gatling2allure-convertor-1.0-SNAPSHOT-jar-with-dependencies.jar

   (проект находится тут: https://bitbucket.region.vtb.ru/projects/DRPIM/repos/gatling2allure-convertor/browse)

2. В pom-файле есть параметры:
   ${myCoolToken} - токен, для работы с TestIT

3. Выполнить mvn clean

4. Запустить тест

5. После завершения выполнения теста, зайти в папку с отчетами:
   cd target/gatling

6. Выполнить команду для переименования директории с отчетом:
   find . -maxdepth 1 -type d -regextype egrep -regex '.*\-[[:digit:]$]{17}' -print -quit | xargs -I {} mv {} mycooldir

7. Запустить конвертор отчетов(из корня проекта):
   java -jar libs/gatling2allure-convertor-1.0-SNAPSHOT-jar-with-dependencies.jar target\\gatling\\mycooldir\\simulation.log
   src\\test\\resources\\links.conf

8. Экспортировать автотесты и отчет в TestIT, выполнив команды:
   mvn test-it:exportAutoTests
   mvn test-it:exportTestPlanResults

