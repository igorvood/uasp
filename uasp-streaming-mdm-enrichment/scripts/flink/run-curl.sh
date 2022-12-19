#!/usr/bin/env bash
#echo "params: $@"
set -e
source .env
#curl -u ${FLINK_USER}:${FLINK_PASS} -k -s -H "Accept: application/json" "$@"
curl -k -s -v -H "Accept: application/json" "$@"