#!/usr/bin/env bash

if [ "$#" -eq 0 ]
  then
    echo "Use example: ./upload-file.sh  ../../target/uasp-streaming-input-convertor-1.1.jar"
    exit
fi

source .env
JAR_FILE=$1
./_run-curl.sh -X POST -F "jarfile=@${JAR_FILE}" ${FLINK_URL}/${API}/jars/upload
