mdm-enrichment.service.name=mdm-enrichment-prof-auth

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

mdm-enrichment.producer.property.transactional.id=enrichment_prof_auth

mdm-enrichment.enrichOne.MainEnrichProperty$.fromTopic=dev_bevents_streaming_input_convertor_profile_auth_uaspdto
mdm-enrichment.enrichOne.MainEnrichProperty$.toTopicName=dev_bevents_card_agreement_enrich_out_uaspdto
mdm-enrichment.enrichOne.MainEnrichProperty$.dlqTopic=dev_bevents_card_agreement_enrich_dlq

mdm-enrichment.enrichOne.CommonEnrichProperty$.fromTopic=dev_rto_batch_ca_customer_card_uaspdto
mdm-enrichment.enrichOne.CommonEnrichProperty$.toTopicName=dev_rto_batch_ca_customer_card_uaspdto__status
mdm-enrichment.enrichOne.CommonEnrichProperty$.dlqTopic=dev_rto_batch_ca_customer_card_uaspdto__dlq
mdm-enrichment.enrichOne.CommonEnrichProperty$.keySelectorMain.isId=false
mdm-enrichment.enrichOne.CommonEnrichProperty$.keySelectorMain.mapType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.keySelectorMain.mapKey=customer_id_and_masked_card_number
mdm-enrichment.enrichOne.CommonEnrichProperty$.keySelectorEnrich.isId=false
mdm-enrichment.enrichOne.CommonEnrichProperty$.keySelectorEnrich.mapType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.keySelectorEnrich.mapKey=customer_id_and_masked_card_number

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a01.fromFieldName=MASK_CARD_NUMBER
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a01.fromFieldType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a01.toFieldName=MASK_CARD_NUMBER
