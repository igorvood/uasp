# Атомарный streaming-сервис uasp-streaming-mdm-enrichment

##Stream-cервис обогащения сообщений way4 глобальным идентификатором из Кросс-ссылок ФЛ (MDM)

Исходные данные:

- app.ref.topic.name - топик с сообщениями локального и глобального id в формате UaspDto
- app.main.topic.name - топик с сообщениями для обогащения формата UaspDto
- app.output.topic.name - топик с обогащенными сообщениями в формате UaspDto
- app.dlq.topic.name - топик с сообщениями в формате UaspDto для которых не найден глобальный id



Пример запуска flink сценария:  
```
uasp-streaming-mdm-enrichment-1.0.jar \
`# Имя сервиса` \
--app.service.name mdm_enrichment \
`# Имя входящего топика с сообщениями cоответсвия id форматаUaspDto  ` \
--app.ref.topic.name streaming_convertor-output_type3 \
`# Имя топика с сообщениями для обогащения формата UaspDto` \
--app.main.topic.name streaming_convertor-output_type1 \
`# Имя топика с сообщениями way4, которые не удалось обогатить` \
--app.dlq.topic.name streaming_mdm_dlq \
`# Исходящий топик с обогащенными сообщениями` \
--app.output.topic.name streaming_enreached \
`# Брокеры кафки` \
--bootstrap.servers kafka1.streaming.tscloud:9092,kafka2.streaming.tscloud:9092,kafka3.streaming.tscloud:9092,kafka4.streaming.tscloud:9092,kafka5.streaming.tscloud:9092 \
`# Креды для подключения к кафки \
--security.protocol SSL \
--enable.auto.commit true \
--ssl.truststore.location C:\\repo\\vtb-projects\\vtb-cloud\\ansible-kafka\\tls\\certs\\kafka-trust.pfx \
--ssl.truststore.password ******** \
--ssl.keystore.location  C:\\repo\\vtb-projects\\vtb-cloud\\ansible-kafka\\tls\\private\\APD00.13.01-USBP-kafka-cluster-1.pfx \
--ssl.keystore.password ******** \
--ssl.key.password ********
```




--mdm-enrichment-sys.max.parallelism 8 
--mdm-enrichment-sys.stream.checkpoint.time.seconds 240000 
--mdm-enrichment-sys.stream.watermark.time.seconds 60 
--mdm-enrichment-sys.stream.window.time.milliseconds 604800000 
--mdm-enrichment.ref.topic.name dev_feature_ivr__uasp_realtime__input_converter__mdm_cross_link__uaspdto 
--mdm-enrichment.ref.status.topic.name dev_feature_ivr__uasp_realtime__mdm_enrichment__mdm_cross_link__status 
--mdm-enrichment.main.topic.name dev_feature_ivr__uasp_realtime__input_converter__way4_issuing_operation__uaspdto 
--mdm-enrichment.dlq.topic.name dev_feature_ivr__uasp_realtime__mdm_enrichment__for_additional_enrichment__uaspdto 
--mdm-enrichment.output.topic.name dev_feature_ivr__uasp_realtime__mdm_enrichment__uaspdto 
--mdm-enrichment.avro.schema.topic.name streaming_avro-schemas 
--mdm-enrichment-sys.bootstrap.servers localhost:9092 
--mdm-enrichment-sys.auto.offset.reset latest 
--mdm-enrichment-sys.isolation.level read_committed 
--mdm-enrichment-sys.enable.idempotence true 
--mdm-enrichment-sys.retries 1 
--mdm-enrichment-sys.acks all 
--mdm-enrichment-sys.transaction.timeout.ms 30000 
--mdm-enrichment-sys.group.id mdm_enrichment_avl
--mdm-enrichment-sys.key.deserializer org.apache.kafka.common.serialization.StringDeserializer 
--mdm-enrichment-sys.value.deserializer org.apache.kafka.common.serialization.ByteArrayDeserializer 
--mdm-enrichment-sys.key.serializer org.apache.kafka.common.serialization.ByteArraySerializer 
--mdm-enrichment-sys.value.serializer org.apache.kafka.common.serialization.ByteArraySerializer 
--mdm-enrichment-sys.ssl.truststore.type PKCS12 
--mdm-enrichment-sys.ssl.keystore.type PKCS12 
--mdm-enrichment-sys.transaction.timeout.ms 900000 
--mdm-enrichment-sys.max.block.ms 90000 
--mdm-enrichment-sys.enable.auto.commit true 
--mdm-enrichment.sync.parallelism 16
--mdm-enrichment.service.name Test