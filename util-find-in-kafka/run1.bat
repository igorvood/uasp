@REM mvn clean package

call java -jar ^
-DBOOTSTRAP_SERVERS=d5uasp-kfc001lk.corp.dev.vtb:9092,d5uasp-kfc002lk.corp.dev.vtb:9092,d5uasp-kfc003lk.corp.dev.vtb:9092,d5uasp-kfc004lk.corp.dev.vtb:9092,d5uasp-kfc005lk.corp.dev.vtb:9092 ^
-DGROUP_ID=gr_1 ^
-DPROTOCOL=SSL ^
-DTRUSTSTORE_LOCATION="new_APD75-None-kafka-d5-client-uasp-truststore.pfx" ^
-DKEYSTORE_LOCATION="new_APD75-None-kafka-d5-client-uasp.pfx" ^
-DKAFKA_SSL_TRUSTSTORE_PASSWORD="p9HrxNIXS4ekSBKKD0Dh<zL7a2!" ^
-DKAFKA_SSL_KEY_PASSWORD="p9HrxNIXS4ekSBKKD0Dh<zL7a2!" ^
-DKAFKA_SSL_KEYSTORE_PASSWORD="p9HrxNIXS4ekSBKKD0Dh<zL7a2!" ^
-DTOPIC_NAME=dev_bevents__realtime__input_converter__prof__transactions__uaspdto ^
-DTOPIC_FIND=gr_1 ^
target\util-find-in-kafka-1.0.10.jar
