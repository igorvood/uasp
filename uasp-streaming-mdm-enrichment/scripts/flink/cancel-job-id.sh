#!/usr/bin/env bash


set -e
source .env

while read line; do
echo "Cancel jobid: ${line} ..."
./run-curl.sh -X PATCH ${FLINK_URL}/${API}/jobs/${line} | jq .
done < ${JOBS_TMP_FILE}

