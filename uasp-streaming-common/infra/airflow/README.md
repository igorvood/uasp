# Airflow compose

## Состав:
1. posgress 13
2. redis latest
3. airflow 2.2.2


## Порты:
1. 6379 - Redis
2. 8080 - Панель управления airflow


## Описание:
Содержит airflow и набор компонентов нужный для его запуска. 
При запуске создаёт папки dags и logs. В которых можно положить новые даги и получить логи.
Дефолтный логин и пароль для входа в панель упарвления airflow: 
-) login: airflow
-) pass: airflow

## Запуска: 
1. docker-compose up --build
2. (Опционально) Положить в папку dags новые даги.
3. Зайти в панель управления по адрес: localhost:8080

## Остановка:
1. docker-compose down --volumes
2. (Опционально) Удалить папки dags и logs

