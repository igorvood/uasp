cd ./scripts/flink
pwd
./upload-jar.sh ../../target/uasp-streaming-model-vector-1.0.jar uasp-streaming-model-vector
./run-jar.sh ../../target/uasp-streaming-model-vector-1.0.jar uasp-streaming-model-vector 4 .env