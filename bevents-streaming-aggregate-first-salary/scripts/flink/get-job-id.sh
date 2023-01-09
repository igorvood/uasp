#!/usr/bin/env bash

set -e
source .env

./run-curl.sh -X GET ${FLINK_URL}/${API}/jobs/overview | jq -r --arg service_name "$SERVICE_NAME" '.jobs[] | select(.name==$service_name and.state=="RUNNING") | .jid' | tee ${JOBS_TMP_FILE}
