#!/usr/bin/env bash

SCHEMA_VERSION=1
SCHEMA_DIR=./schemas
KAFKA_PATH=/opt/tools/kafka/kafka_2.13-3.1.0

#Exit immediately if a command exits with a non-zero status.
set -e

#cp -f ift.env .env
source .env

echo ${SOURCE_ID}
#exit

if [ $# -eq 0 ]
  then
    echo -e "Load json or avro schema in specific kafka topic \nUse example: \n\t put-schema.sh json \n\t put-schema avro "
    exit
fi


if [ $1 = "json" ]; then
  SCHEMA_TOPIC=${JSON_SCHEMA_TOPIC}

elif [ $1 = "avro" ]; then
  SCHEMA_TOPIC=${AVRO_SCHEMA_TOPIC}
else
  echo "First parameter must be 'json' or 'avro'"
  exit
fi
SCHEMA_KEY=${SOURCE_ID}-${SCHEMA_VERSION}
SCHEMA_FILE=${SCHEMA_DIR}/${1}schema_${SCHEMA_KEY}.json

echo "SCHEMA_TOPIC: $SCHEMA_TOPIC"
echo "SCHEMA_KEY  : $SCHEMA_KEY"
echo "SCHEMA_FILE : $SCHEMA_FILE"

if [ ! -f "$SCHEMA_FILE" ]; then
    echo "$SCHEMA_FILE does not exist."
    exit
fi

echo "Add key to file with value ..."
printf "${SCHEMA_KEY}|" > tmp.file
cat ${SCHEMA_FILE} >> tmp.file
#Delete newlines because kafka-console-producer doesn't make multiline messages
cat tmp.file | tr -d '\n' | tr -d ' ' > mes.file
rm -f tmp.file
cat mes.file

${KAFKA_PATH}/bin/kafka-console-producer.sh \
  --broker-list ${BOOTSTRAP_SERVERS} \
  --topic ${SCHEMA_TOPIC} \
  --property parse.key=true \
  --property key.separator="|" < mes.file \
  --producer.config ./config/ift.properties
