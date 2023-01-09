#!/usr/bin/env bash
set -e

if [ "$#" -ne 2 ]
  then
    echo "Use example: ./upload-jar.sh ../../target/uasp-streaming-input-convertor-1.1.jar service_name"
    exit
fi

JAR_FILE=$1
SERVICE_NAME=$2
FILE_NAME=$(basename ${JAR_FILE})

if [ ! -f "$JAR_FILE" ]; then
    echo "$JAR_FILE does not exist."
    exit
fi

if [ -z "$SERVICE_NAME" ]
then
  echo "SERVICE_NAME is empty. Skip cancellation step"
else
  echo -e "\n===== Cancel service with name ${SERVICE_NAME}"
  ./_cancel-service-with-name.sh ${SERVICE_NAME}
fi

echo -e "\n===== Delete old jars"
  ./_delete-jar-with-name.sh ${FILE_NAME}

echo -e "\n===== Upload new jar: ${JAR_FILE}"
  ./_upload-file.sh ${JAR_FILE}





