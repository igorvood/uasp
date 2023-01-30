#!/usr/bin/env bash

FLINK_PATH=/home/tsg/flink
JAR_FILE=/mnt/c/repo/vtb-projects/vtb-cloud/uasp/uasp-streaming-input-convertor/target/uasp-streaming-input-convertor-1.1.jar
MAIN_CLASS=ru.vtb.uasp.inputconvertor.Convertor
JOB_MANAGER_HOST=10.12.8.75:8081
SECRET=********
PARALLELISM=4

clear
${FLINK_PATH}/bin/flink run  \
-p ${PARALLELISM} \
-c ${MAIN_CLASS} \
-m ${JOB_MANAGER_HOST} \
${JAR_FILE} \
--app.service.name way4_convertor \
--app.input.topic.name streaming_convertor-input_1 \
--app.output.topic.name streaming_convertor-output_1 \
--app.dlq.topic.name streaming_dlq \
--app.json.schema.topic.name streaming_json-schemas \
--app.avro.schema.topic.name streaming_avro-schemas \
--app.schema.name type1 \
--app.jsonschema.start.version 1 \
--app.avroschema.start.version 1 \
--bootstrap.servers kafka1.streaming.tscloud:9092,kafka2.streaming.tscloud:9092,kafka3.streaming.tscloud:9092,kafka4.streaming.tscloud:9092,kafka5.streaming.tscloud:9092 \
--group.id way4_convertor \
--security.protocol SSL \
--enable.auto.commit true \
--ssl.truststore.location "/opt/kafka-certs/kafka-trust.pfx" \
--ssl.truststore.password ${SECRET} \
--ssl.keystore.location "/opt/kafka-certs/APD00.13.01-USBP-kafka-cluster-uasp.pfx" \
--ssl.keystore.password ${SECRET}  \
--ssl.key.password ${SECRET} \
--ssl.truststore.type PKCS12 \
--ssl.keystore.type PKCS12


#--key.deserializer "org.apache.kafka.common.serialization.StringDeserializer" \
#--value.deserializer "org.apache.kafka.common.serialization.ByteArrayDeserializer" \
#--key.serializer "org.apache.kafka.common.serialization.StringSerializer" \
#--value.serializer "org.apache.kafka.common.serialization.ByteArraySerializer" \








