#!/usr/bin/env bash

#SCHEMA_DIR=./schemas

#Exit immediately if a command exits with a non-zero status.
set -e


if [ $# -ne 2 ]
  then
    echo -e "example: ./single-schema.sh ift schemas/issuing-client-1.3.json"
    exit 1
fi

source ./${1}.env

FILENAME=$(basename "$2")
SCHEMA_KEY="${FILENAME%.*}"
SCHEMA_FILE=$2
KAFKA_CONFIG=../config/${1}.properties

echo "SCHEMA_TOPIC: $SCHEMA_TOPIC"
echo "SCHEMA_KEY  : $SCHEMA_KEY"
echo "SCHEMA_FILE : $SCHEMA_FILE"
echo "KAFKA_CONFIG: $KAFKA_CONFIG"


if [ ! -f "$SCHEMA_FILE" ]; then
    echo "$SCHEMA_FILE does not exist."
    exit 1
fi

if [ ! -f "$KAFKA_CONFIG" ]; then
    echo "$KAFKA_CONFIG does not exist."
    exit 1
fi

echo "Add key to file with value ..."
printf "${SCHEMA_KEY}|" > tmp.file
cat ${SCHEMA_FILE} >> tmp.file
#Delete newlines because kafka-console-producer doesn't make multiline messages
cat tmp.file | tr -d '\n' | tr -d ' ' > mes.file
rm -f tmp.file
cat mes.file

 /mnt/c/Users/rdzhondzhua/temp/kafka_2.12-3.1.0/bin/kafka-console-producer.sh \
  --broker-list ${BOOTSTRAP_SERVERS} \
  --topic ${SCHEMA_TOPIC} \
  --property parse.key=true \
  --property key.separator="|" < mes.file \
  --producer.config ${KAFKA_CONFIG}
