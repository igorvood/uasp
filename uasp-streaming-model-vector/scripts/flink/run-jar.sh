#!/usr/bin/env bash
set -e

if [ "$#" -ne 4 ]
  then
    echo "Use example: ./run-jar.sh ../../target/uasp-streaming-input-convertor-1.1.jar service_name parallelism envfile"
    exit
fi
SLEEP_SEC=5
JAR_FILE=$1
SERVICE_NAME=$2
PARALLELISM=$3
ENV_FILE=$4

FILE_NAME=$(basename ${JAR_FILE})

if [ ! -f "$JAR_FILE" ]; then
    echo "$JAR_FILE does not exist."
    exit
fi

echo -e "\n===== 1. Cancel service with name ${SERVICE_NAME}"
./_cancel-service-with-name.sh ${SERVICE_NAME}

echo -e "\n===== 2. Find out JAR_ID for ${FILE_NAME}"
JAR_ID=$(./_get-jar-id.sh ${FILE_NAME})
if [ -z "$JAR_ID" ]; then
  echo "JAR_ID is empty. ${JAR_FILE} not found. Exit!"
  exit 1
fi

echo -e "\n===== 3. Run job for JAR_ID: ${JAR_ID}"
JOB_ID=$(./_run-job.sh ${ENV_FILE} ${SERVICE_NAME} ${PARALLELISM} ${JAR_ID})
if [ -z "$JOB_ID" ]
then
    echo "Error running job from $JAR_ID. See logs on cluster. Exit!"
    exit 1
fi

echo -e "\nSleep ${SLEEP_SEC} seconds..."
sleep ${SLEEP_SEC}

echo -e "\n===== 4. Check status for ${JOB_ID}"
DEATAIL=$(./_detail-by-jobid.sh ${JOB_ID})
echo "${DEATAIL}" | jq .
STATE=$(echo "${DEATAIL}" | jq -r .state)
if [ "RUNNING" != "$STATE" ]; then
    echo "Running is fail. Exit!"
    exit 1
fi
echo "Congratulations! Successful launch ${SERVICE_NAME}!"




