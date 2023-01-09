#!/usr/bin/env bash

set -e
source .env

FILE_NAME=$1

./get-jar-id.sh ${FILE_NAME}

while read line; do
echo "Delete jar id: ${line} ..."
./run-curl.sh -X DELETE ${FLINK_URL}/${API}/jars/${line}
done < ${JARS_TMP_FILE}