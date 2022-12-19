mdm-enrichment.sync.parallelism=8

mdm-enrichment.service.name=mdm-enrichment-step2
mdm-enrichment.enrichOne.MainEnrichProperty$.cns.group.id=mdm-enrichment-step2_3

#mdm-enrichment.enrichOne.MainEnrichProperty$.cns.bootstrap.servers=d5uasp-apc003lk.corp.dev.vtb:9092,d5uasp-apc003lk.corp.dev.vtb:9092,d5uasp-apc004lk.corp.dev.vtb:9092,d5uasp-apc018lk.corp.dev.vtb:9092,d5uasp-apc025lk.corp.dev.vtb:9092
#mdm-enrichment.enrichOne.MainEnrichProperty$.cns.security.protocol=SSL

mdm-enrichment.enrichOne.MainEnrichProperty$.cns.bootstrap.servers=172.20.219.153:9092,172.20.219.153:9091
mdm-enrichment.enrichOne.MainEnrichProperty$.cns.security.protocol=PLAINTEXT
mdm-enrichment.enrichOne.MainEnrichProperty$.cns.ssl.key.password=kafkauasppassword
mdm-enrichment.enrichOne.MainEnrichProperty$.cns.ssl.keystore.password=kafkauasppassword
mdm-enrichment.enrichOne.MainEnrichProperty$.cns.ssl.truststore.password=kafkauasppassword
mdm-enrichment.enrichOne.MainEnrichProperty$.cns.ssl.keystore.location=C:\\Work\\SSL\\APD00.13.01-USBP-kafka-cluster-uasp.pfx
mdm-enrichment.enrichOne.MainEnrichProperty$.cns.ssl.truststore.location=C:\\Work\\SSL\\kafka-trust.pfx


mdm-enrichment.producer.property.bootstrap.servers=172.20.219.153:9092,172.20.219.153:9091
mdm-enrichment.producer.property.security.protocol=PLAINTEXT

mdm-enrichment.producer.property.ssl.key.password=kafkauasppassword
mdm-enrichment.producer.property.ssl.keystore.password=kafkauasppassword
mdm-enrichment.producer.property.ssl.truststore.password=kafkauasppassword
mdm-enrichment.producer.property.ssl.keystore.location=C:\\Work\\SSL\\APD00.13.01-USBP-kafka-cluster-uasp.pfx
mdm-enrichment.producer.property.ssl.truststore.location=C:\\Work\\SSL\\kafka-trust.pfx

mdm-enrichment.producer.property.transactional.id=enrichment_transactional_id_second

mdm-enrichment.enrichOne.MainEnrichProperty$.fromTopic=dev_bevents__realtime__enrichment__prof__contract_num__uaspdto
mdm-enrichment.enrichOne.MainEnrichProperty$.toTopicName=dev_bevents__realtime__enrichment__prof__transactions_first__uaspdto
mdm-enrichment.enrichOne.MainEnrichProperty$.dlqTopic=dev_bevents__realtime__enrichment__prof__transactions_with_partyUId__dlq

mdm-enrichment.enrichOne.GlobalIdEnrichProperty$.fromTopic=dev_bevents__batch__ca_regulatory_contract_num_of_mdm_id_profile__uaspdto
mdm-enrichment.enrichOne.GlobalIdEnrichProperty$.dlqTopic=dev_bevents__realtime__enrichment__prof__transactions_with_mdm_id__dlq
mdm-enrichment.enrichOne.GlobalIdEnrichProperty$.keySelectorMain.isId=true
mdm-enrichment.enrichOne.GlobalIdEnrichProperty$.keySelectorEnrich.isId=true
mdm-enrichment.enrichOne.GlobalIdEnrichProperty$.inputDataFormat=UaspDtoFormat

mdm-enrichment.enrichOne.GlobalIdEnrichProperty$.globalEnrichFields.fromFieldName=global_id
mdm-enrichment.enrichOne.GlobalIdEnrichProperty$.globalEnrichFields.fromFieldType=String
mdm-enrichment.enrichOne.GlobalIdEnrichProperty$.globalEnrichFields.toFieldName=calculate-mdm_id
mdm-enrichment.enrichOne.GlobalIdEnrichProperty$.globalEnrichFields.isOptionalEnrichValue=false

mdm-enrichment.enrichOne.GlobalIdEnrichProperty$.cns.auto.offset.reset=earliest
mdm-enrichment.enrichOne.GlobalIdEnrichProperty$.cns.bootstrap.servers=172.20.219.153:9092,172.20.219.153:9091
mdm-enrichment.enrichOne.GlobalIdEnrichProperty$.cns.security.protocol=PLAINTEXT
mdm-enrichment.enrichOne.GlobalIdEnrichProperty$.cns.ssl.key.password=kafkauasppassword
mdm-enrichment.enrichOne.GlobalIdEnrichProperty$.cns.ssl.keystore.password=kafkauasppassword
mdm-enrichment.enrichOne.GlobalIdEnrichProperty$.cns.ssl.truststore.password=kafkauasppassword
mdm-enrichment.enrichOne.GlobalIdEnrichProperty$.cns.ssl.keystore.location=C:\\Work\\SSL\\APD00.13.01-USBP-kafka-cluster-uasp.pfx
mdm-enrichment.enrichOne.GlobalIdEnrichProperty$.cns.ssl.truststore.location=C:\\Work\\SSL\\kafka-trust.pfx
mdm-enrichment.enrichOne.GlobalIdEnrichProperty$.cns.isolation.level=read_uncommitted
mdm-enrichment.enrichOne.GlobalIdEnrichProperty$.cns.ssl.keystore.type=PKCS12
mdm-enrichment.enrichOne.GlobalIdEnrichProperty$.cns.ssl.truststore.type=PKCS12
mdm-enrichment.enrichOne.GlobalIdEnrichProperty$.cns.session.timeout.ms=72000000
mdm-enrichment.enrichOne.GlobalIdEnrichProperty$.cns.fetch.min.bytes=50