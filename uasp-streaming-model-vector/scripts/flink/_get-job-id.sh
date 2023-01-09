#!/usr/bin/env bash

if [ $# -eq 0 ]
then
    echo "Use example: ./get-job-id.sh service_name"
    exit
fi

set -e
source .env
SERVICE_NAME=$1
./_run-curl.sh -X GET ${FLINK_URL}/${API}/jobs/overview | jq -r --arg service_name "$SERVICE_NAME" '.jobs[] | select(.name==$service_name and.state=="RUNNING") | .jid'
# | tee ${JOBS_TMP_FILE}
