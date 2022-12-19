Для EXACTLY-ONCE, обязательные параметры на продюсере Kafka:
"--enable.idempotence true "
"--max.in.flight.requests.per.connection 5 "
"--retries 1 "
"--acks all "