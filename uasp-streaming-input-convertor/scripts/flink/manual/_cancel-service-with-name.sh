#!/usr/bin/env bash

if [ $# -eq 0 ]
then
    echo "Use example: ./cancel-service-with-name.sh service_name"
    exit
fi

SERVICE_NAME=$1
echo "SERVICE_NAME: $SERVICE_NAME"
set -e


JOB_ID=$(./_get-job-id.sh ${SERVICE_NAME})
echo "JOB_ID: $JOB_ID"

if [ -z "$JOB_ID" ]
then
  echo "JOB_ID is empty, ${SERVICE_NAME} not found"
else
  echo -e "\n Cancel jobs with id ${JOB_ID} ..."
  ./_cancel-job-id.sh ${JOB_ID}
  sleep 3
fi


