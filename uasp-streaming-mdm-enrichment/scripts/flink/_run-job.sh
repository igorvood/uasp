#!/usr/bin/env bash

source .env

if [ "$#" -eq 3 ]
  then
    echo "Use example: run-job.sh envfile service_name parallelism jarid"
    exit
fi


SERVICE_NAME=$2
PARALLELISM=$3
JAR_ID=$4

SERVICE_NAME="$SERVICE_NAME" PARALLELISM="$PARALLELISM" source $1

generate_post_data()
{
  cat <<EOF
{
  "entryClass": "${MAIN_CLASS}",
  "parallelism": "${PARALLELISM}",
  "programArgs": "${PROGRAMARGS}"
}
EOF
}

#echo -e "\nDATA:\n"
#generate_post_data | jq .

#echo "Run jar id: $JAR_ID ..."
./_run-curl.sh -X POST -d "$(generate_post_data)" ${FLINK_URL}/${API}/jars/${JAR_ID}/run | jq -r '.jobid'

