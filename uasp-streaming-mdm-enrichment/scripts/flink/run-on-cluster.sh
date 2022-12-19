#!/usr/bin/env bash
clear
source .env-itest
set -e
JAR_FILE=$1
SLEEP_SEC=5

generate_post_data()
{
  cat <<EOF
{
  "entryClass": "${MAIN_CLASS}",
  "parallelism": "${PARALLELISM}",
  "programArgs": "${PROGRAMARGS}"
}
EOF
}

if [ $# -eq 0 ]
  then
    echo "Use example: ./run-on-cluster.sh ../../target/uasp-streaming-aggregate-1.0.jar"
    exit
fi


if [ ! -f "$JAR_FILE" ]; then
    echo "$JAR_FILE does not exist."
    exit
fi

echo -e "\nDATA:\n"
generate_post_data | jq .

FILE_NAME=$(basename ${JAR_FILE})

echo -e "\n Cancel jobs with name ${SERVICE_NAME} ..."
./get-job-id.sh
#./detail-by-jobid.sh
./cancel-job-id.sh

echo -e "\nDelete old jars"
./delete-jars-with-name.sh ${FILE_NAME}

echo -e "\nUpload new jar: ${JAR_FILE} ..."
./upload-file.sh ${JAR_FILE}

echo -e "\nFind id jar"
./get-jar-id.sh ${FILE_NAME}


echo "Run on cluster"
while read line; do
echo "Run jar id: ${line} ..."
jobid=$(./run-curl.sh -X POST -d "$(generate_post_data)" ${FLINK_URL}/${API}/jars/${line}/run | jq -r '.jobid')
echo "jobid: ${jobid}"
done < ${JARS_TMP_FILE}


echo -e "\nSleet ${SLEEP_SEC} sec..."
sleep ${SLEEP_SEC}

echo -e "\nCheck status ${FILE_NAME}"
./get-job-id.sh
./detail-by-jobid.sh



