#flink run -c ru.vtb.uasp.pilot.model.vector.UaspStreamingModelVectorPilot -m  10.12.8.75:8081 /mnt/c/users/ebelysheva/IdeaProjects/uasp-streaming-model-vector-pilot/target/uasp-streaming-model-vector-pilot-1.0.jar --stream.checkpoint.time.seconds 120 --stream.watermark.time.seconds 60 --stream.window.time.milliseconds 604800000 --consumer.topicHot.name pilot-streaming-model-vector-data-hot --consumer.topicCold.name pilot-streaming-model-vector-data-cold --producer.topic.name pilot-streaming-model-vector-data-result --bootstrap.servers kafka1.streaming.tscloud:9092,kafka2.streaming.tscloud:9092,kafka3.streaming.tscloud:9092,kafka4.streaming.tscloud:9092,kafka5.streaming.tscloud:9092 --group.id uasp-streaming-model-vector-pilot --security.protocol SSL --ssl.truststore.location /opt/kafka-certs/kafka-trust.pfx --ssl.truststore.password ***** --ssl.keystore.location /opt/kafka-certs/APD00.13.01-USBP-kafka-cluster-1.pfx --ssl.keystore.password ***** --ssl.key.password ***** --key.deserializer "org.apache.kafka.common.serialization.StringDeserializer" --value.deserializer "org.apache.kafka.common.serialization.ByteArrayDeserializer" --key.serializer "org.apache.kafka.common.serialization.StringSerializer" --value.serializer "org.apache.kafka.common.serialization.ByteArraySerializer"
cd ./scripts/flink
pwd
./run-on-cluster.sh ./../../target/uasp-streaming-aggregate-1.0.jar
