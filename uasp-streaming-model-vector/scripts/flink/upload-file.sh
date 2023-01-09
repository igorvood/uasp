#!/usr/bin/env bash

if [ $# -eq 0 ]
  then
    echo "Use example: ./upload-file.sh ../../target/uasp-streaming-input-convertor-1.0.jar"
    exit
fi

source .env
JAR_FILE=$1
#echo "FLINK_URL: ${FLINK_URL}"
#echo "JAR_FILE: ${JAR_FILE}"
#./run-curl.sh -X POST -H "Expect:" -F "jarfile=@${JAR_FILE}" ${FLINK_URL}/${API}/jars/upload
./run-curl.sh -X POST -F "jarfile=@${JAR_FILE}" ${FLINK_URL}/${API}/jars/upload