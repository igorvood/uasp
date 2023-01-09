#!/usr/bin/env bash

if [ $# -eq 0 ]
then
    echo "Use example: ./cancel-job-id job-id"
    exit
fi

set -e
source .env
./_run-curl.sh -X PATCH ${FLINK_URL}/${API}/jobs/$1 | jq .


