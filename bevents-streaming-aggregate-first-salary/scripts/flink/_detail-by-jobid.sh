#!/usr/bin/env bash
if [ $# -eq 0 ]
then
    echo "Use example: ./detail-by-jobid.sh jobid"
    exit
fi

JOB_ID=$1
set -e
source .env

./_run-curl.sh -X GET ${FLINK_URL}/${API}/jobs/${JOB_ID}
