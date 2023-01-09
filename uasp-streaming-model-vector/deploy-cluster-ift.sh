cd ./scripts/flink
pwd
./upload-jar.sh ../../target/uasp-streaming-model-vector-1.0.latest.jar uasp-streaming-model-vector
./run-jar.sh ../../target/uasp-streaming-model-vector-1.0.latest.jar uasp-streaming-model-vector 4 .app-env-ift
