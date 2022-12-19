bussines.rulles.flink.job.checkpoint.service.name=mutator_for_case68
bussines.rulles.kafka.consumer.property.group.id=mutator_for_case68

bussines.rulles.drools.list=currency-rate-for-uddt.drl

bussines.rulles.filter.fieldName=sys-BussinesRulles-error
bussines.rulles.filter.operandClass=String
bussines.rulles.filter.operator=null
bussines.rulles.filter.tagPrefix=filterErrorTag

bussines.rulles.kafka.consumer.topicName=dev_bevents_cft_way4_profile_udds_after_mutator_uaspdto

bussines.rulles.kafka.producers.filterErrorTag-error.topicName=dev_bevents_cft_way4_profile_udds_before_mdm_rate_case68_uaspdto_dlq
bussines.rulles.kafka.producers.filterErrorTag-success.topicName=dev_bevents_cft_way4_profile_udds_before_mdm_rate_case68_uaspdto

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
