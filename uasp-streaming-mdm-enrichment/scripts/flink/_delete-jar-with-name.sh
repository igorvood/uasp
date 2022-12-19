#!/usr/bin/env bash

if [ $# -eq 0 ]
then
    echo "Use example: ./delete-jars-with-name.sh file_name"
    exit
fi

set -e
source .env

FILE_NAME=$1
echo "FILE_NAME: $FILE_NAME"

JAR_ID=$(./_get-jar-id.sh ${FILE_NAME})
#echo "JAR_ID: $JAR_ID"
if [ -z "$JAR_ID" ]; then
  echo "JAR_ID is empty"
else
  echo "Delete jar id: $JAR_ID ..."
  ./_run-curl.sh -X DELETE ${FLINK_URL}/${API}/jars/${JAR_ID}
fi


