#!/usr/bin/env bash


set -e
source .env

while read line; do
echo "Detail info for jobid: ${line} ..."
./run-curl.sh -X GET ${FLINK_URL}/${API}/jobs/${line} | jq .
done < ${JOBS_TMP_FILE}
