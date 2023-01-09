#!/usr/bin/env bash

if [ $# -eq 0 ]
then
    echo "Use example: ./get-jar-id.sh uasp-streaming-input-convertor-1.1.jar"
    exit
fi

set -e
source .env

FILE_NAME=$1

./_run-curl.sh -X GET ${FLINK_URL}/${API}/jars | jq -r --arg file_name "$FILE_NAME" '.files[] | select(.name==$file_name) | .id'
# | tee ${JARS_TMP_FILE}
#if ! [ -s $"JARS_TMP_FILE" ] && echo -e "\n\n${JARS_TMP_FILE} is empty. No jar files uploaded" && exit 1

