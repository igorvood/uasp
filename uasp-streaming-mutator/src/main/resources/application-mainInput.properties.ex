bussines.rulles.flink.job.checkpoint.service.name=mutator_for_main_input
bussines.rulles.kafka.consumer.property.group.id=mutator_for_main_input

bussines.rulles.drools.list=way4-case-2_2.drl,way4-case-2_3.drl,way4-case-2_4.drl,way4-case-2_10.drl,way4-case-5_2.drl,way4-case-5_3.drl,way4-case-11_2.drl,POS.drl,Salary.drl,FirstPension.drl,NS.drl

bussines.rulles.filter.fieldName=sys-BussinesRulles-error
bussines.rulles.filter.operandClass=String
bussines.rulles.filter.operator=null
bussines.rulles.filter.tagPrefix=filterErrorTag

bussines.rulles.kafka.consumer.topicName=dev_ivr__uasp_realtime_way4_mdm_enrichment__uaspdto

bussines.rulles.kafka.producers.filterErrorTag-error.topicName=dev_ivr__uasp_realtime__bussiness_rules__uaspdto__dlq
bussines.rulles.kafka.producers.filterErrorTag-success.topicName=dev_ivr__uasp_realtime__business_rules__uaspdto


bussines.rulles.kafka.consumer.property.security.protocol=SSL
bussines.rulles.kafka.consumer.property.ssl.key.password=${DSO_KAFKA_SSL_KEY_PASSWORD}
bussines.rulles.kafka.consumer.property.ssl.keystore.location=${KEYSTORE_LOCATION}
bussines.rulles.kafka.consumer.property.ssl.keystore.password=${DSO_KAFKA_SSL_KEYSTORE_PASSWORD}
bussines.rulles.kafka.consumer.property.ssl.truststore.location=${TRUSTSTORE_LOCATION}
bussines.rulles.kafka.consumer.property.ssl.truststore.password=${DSO_KAFKA_SSL_TRUSTSTORE_PASSWORD}
bussines.rulles.kafka.consumer.property.bootstrap.servers=${BOOTSTRAP_SERVERS}

bussines.rulles.kafka.producer.security.protocol=SSL
bussines.rulles.kafka.producer.ssl.key.password=${DSO_KAFKA_SSL_KEY_PASSWORD}
bussines.rulles.kafka.producer.ssl.keystore.location=${KEYSTORE_LOCATION}
bussines.rulles.kafka.producer.ssl.keystore.password=${DSO_KAFKA_SSL_KEYSTORE_PASSWORD}
bussines.rulles.kafka.producer.ssl.truststore.location=${TRUSTSTORE_LOCATION}
bussines.rulles.kafka.producer.ssl.truststore.password=${DSO_KAFKA_SSL_TRUSTSTORE_PASSWORD}
bussines.rulles.kafka.producer.bootstrap.servers=${BOOTSTRAP_SERVERS}
