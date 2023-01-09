# uasp-streaming-model-vector-pilot

Stream-cервис сбора полного вектора модели данных для пилота


ru.vtb.uasp.pilot.model.vector.UaspStreamingModelVector

--max.parallelism 1 
--stream.checkpoint.time.seconds 120000 
--stream.watermark.time.seconds 60 
--stream.window.time.milliseconds 604800000 
--consumer.topic.name dev_ivr__uasp_realtime__ca_ha_aggregate__uaspdto 
--producer.qa.topic.name dev_ivr__uasp_realtime__model_vector__json__itest 
--producer.topic.name dev_ivr__uasp_realtime__model_vector__json 
--producer.dlq.topic.name dev_ivr__uasp_realtime__model_vector__dlq 
--preffix test 
--enable.qa.stream true 
--bootstrap.servers localhost:9092 
--group.id uasp-streaming-model-vector
--key.deserializer org.apache.kafka.common.serialization.StringDeserializer 
--value.deserializer org.apache.kafka.common.serialization.ByteArrayDeserializer 
--key.serializer org.apache.kafka.common.serialization.ByteArraySerializer 
--value.serializer org.apache.kafka.common.serialization.ByteArraySerializer 
--transaction.timeout.ms 1200000 
--max.block.ms 90000 
--enable.auto.commit true 
--enable.idempotence true 
--max.in.flight.requests.per.connection 5 
--retries 1 
--acks all 
--uid-prefix model-vector 
--isolation.level read_committed 
--state.checkpoints.num-retained 4 
--transaction.max.timeout.ms 1200000
--app.service.name test