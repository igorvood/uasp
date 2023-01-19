mdm-enrichment.sync.parallelism=8

mdm-enrichment.service.name=mdm-enrichment-prof-auth-packNM
mdm-enrichment.enrichOne.MainEnrichProperty$.cns.group.id=mdm-enrichment-prof-auth-packNM_3

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

mdm-enrichment.producer.property.transactional.id=enrichment_prof_auth_packNM

mdm-enrichment.enrichOne.MainEnrichProperty$.fromTopic=dev_bevents_card_agreement_enrich_out_uaspdto
mdm-enrichment.enrichOne.MainEnrichProperty$.toTopicName=dev_bevents__realtime__enrichment__prof__transactions_first__uaspdto
mdm-enrichment.enrichOne.MainEnrichProperty$.dlqTopic=dev_bevents_package_nm_enrich_dlq

mdm-enrichment.enrichOne.CommonEnrichProperty$.fromTopic=dev__dko_uasp__pension_converted
mdm-enrichment.enrichOne.CommonEnrichProperty$.dlqTopic=dev_rto_batch_ca_customer_package_dlq
mdm-enrichment.enrichOne.CommonEnrichProperty$.keySelectorMain.isId=true
mdm-enrichment.enrichOne.CommonEnrichProperty$.keySelectorEnrich.isId=true
mdm-enrichment.enrichOne.CommonEnrichProperty$.inputDataFormat=UaspDtoFormat

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a01.fromFieldName=package_nm
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a01.fromFieldType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a01.toFieldName=package_nm
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a01.isOptionalEnrichValue=true
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a02.fromFieldName=multibonus_flg
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a02.fromFieldType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a02.toFieldName=multibonus_flg
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a02.isOptionalEnrichValue=true

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a02.fromFieldName=multibonus_flg
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a02.fromFieldType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a02.toFieldName=multibonus_flg
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a02.isOptionalEnrichValue=true

mdm-enrichment.enrichOne.CommonEnrichProperty$.cns.auto.offset.reset=earliest
mdm-enrichment.enrichOne.CommonEnrichProperty$.cns.bootstrap.servers=172.20.219.153:9092,172.20.219.153:9091
mdm-enrichment.enrichOne.CommonEnrichProperty$.cns.security.protocol=PLAINTEXT
mdm-enrichment.enrichOne.CommonEnrichProperty$.cns.ssl.key.password=kafkauasppassword
mdm-enrichment.enrichOne.CommonEnrichProperty$.cns.ssl.keystore.password=kafkauasppassword
mdm-enrichment.enrichOne.CommonEnrichProperty$.cns.ssl.truststore.password=kafkauasppassword
mdm-enrichment.enrichOne.CommonEnrichProperty$.cns.ssl.keystore.location=C:\\Work\\SSL\\APD00.13.01-USBP-kafka-cluster-uasp.pfx
mdm-enrichment.enrichOne.CommonEnrichProperty$.cns.ssl.truststore.location=C:\\Work\\SSL\\kafka-trust.pfx
mdm-enrichment.enrichOne.CommonEnrichProperty$.cns.isolation.level=read_uncommitted
mdm-enrichment.enrichOne.CommonEnrichProperty$.cns.ssl.keystore.type=PKCS12
mdm-enrichment.enrichOne.CommonEnrichProperty$.cns.ssl.truststore.type=PKCS12
mdm-enrichment.enrichOne.CommonEnrichProperty$.cns.session.timeout.ms=72000000
mdm-enrichment.enrichOne.CommonEnrichProperty$.cns.fetch.min.bytes=50

