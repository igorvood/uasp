#!/usr/bin/env bash
DEPLOY_SCRIPTS=flink-deployment

rm -rf ${DEPLOY_SCRIPTS}/
set -e
cp -r ../${DEPLOY_SCRIPTS} ./
cd ${DEPLOY_SCRIPTS}/scripts
find . -iname "*.sh" -exec chmod +x {} \;
./deploy2stage.sh

#cd ./scripts/flink
#pwd
#cp -f ift.env .env
#
#
#./upload-jar.sh ../../target/uasp-streaming-input-convertor-1.1.latest.jar uasp_streaming_way4_convertor
#./run-jar.sh ../../target/uasp-streaming-input-convertor-1.1.latest.jar uasp_streaming_way4_convertor 8 ift-way4.env
#./run-jar.sh ../../target/uasp-streaming-input-convertor-1.1.latest.jar uasp_streaming_mdm_convertor 8 ift-mdm.env