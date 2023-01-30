#!/usr/bin/env bash

set -e

cd ./scripts/flink
pwd
cp -f ift.env .env
 

./upload-jar.sh ../../target/uasp-streaming-input-convertor-1.1.latest.jar uasp_streaming_way4_convertor
./run-jar.sh ../../target/uasp-streaming-input-convertor-1.1.latest.jar uasp_streaming_way4_convertor 8 ift-way4.env
./run-jar.sh ../../target/uasp-streaming-input-convertor-1.1.latest.jar uasp_streaming_mdm_convertor 8 ift-mdm.env