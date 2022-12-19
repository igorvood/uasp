


filter.kafka.security.protocol=SSL
filter.kafka.bootstrap.servers=d5uasp-kfc001lk.corp.dev.vtb:9092,d5uasp-kfc002lk.corp.dev.vtb:9092,d5uasp-kfc003lk.corp.dev.vtb:9092,d5uasp-kfc004lk.corp.dev.vtb:9092,d5uasp-kfc005lk.corp.dev.vtb:9092,d5uasp-kfc006lk.corp.dev.vtb:9092

filter.kafka.ssl.truststore.location=C:\\Work\\SSL\\new_APD75-None-kafka-d5-client-uasp-truststore.pfx
filter.kafka.ssl.keystore.location=C:\\Work\\SSL\\new_APD75-None-kafka-d5-client-uasp.pfx

filter.kafka.ssl.truststore.password=pswd
filter.kafka.ssl.keystore.password=pswd
filter.kafka.ssl.key.password=pswd

filter.app.kafka.consumer.property.group.id=Filter-way4
filter.app.kafka.consumer.property.security.protocol=SSL
filter.app.kafka.consumer.property.bootstrap.servers=d5uasp-kfc001lk.corp.dev.vtb:9092,d5uasp-kfc002lk.corp.dev.vtb:9092,d5uasp-kfc003lk.corp.dev.vtb:9092,d5uasp-kfc004lk.corp.dev.vtb:9092,d5uasp-kfc005lk.corp.dev.vtb:9092,d5uasp-kfc006lk.corp.dev.vtb:9092

filter.app.kafka.consumer.property.ssl.key.password=pswd
filter.app.kafka.consumer.property.ssl.keystore.location=C:\\Work\\SSL\\new_APD75-None-kafka-d5-client-uasp.pfx
filter.app.kafka.consumer.property.ssl.keystore.password=pswd
filter.app.kafka.consumer.property.ssl.truststore.location=C:\\Work\\SSL\\new_APD75-None-kafka-d5-client-uasp-truststore.pfx
filter.app.kafka.consumer.property.ssl.truststore.password=pswd

filter.app.flink.job.checkpoint.service.name=filter-dko

filter.app.kafka.consumer.topicName=dev_ivr__uasp_realtime__business_rules__uaspdto

filter.app.kafka.producers.filterTag-success.topicName=dev_ivr__uasp_realtime__mdm_enrichment__uaspdto
filter.app.kafka.producers.filterTag-error.topicName=dev_ivr__uasp_realtime__filter__uaspdto__filter

filter.app.filter.fieldName=system-uasp-way-classification
filter.app.filter.operandClass=String
filter.app.filter.operator=notNull
filter.app.filter.tagPrefix=filterTag


