mdm-enrichment.sync.parallelism=8

mdm-enrichment.service.name=mdm-enrichment-case-68
mdm-enrichment.enrichOne.MainEnrichProperty$.cns.group.id=mdm-enrichment-case-68_3

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

mdm-enrichment.producer.property.transactional.id=enrichment_case_68

mdm-enrichment.enrichOne.MainEnrichProperty$.fromTopic=dev_bevents_cft_way4_profile_udds_before_mdm_rate_case68_uaspdto
mdm-enrichment.enrichOne.MainEnrichProperty$.toTopicName=dev_bevents_cft_way4_profile_udds_before_case68_uaspdto
mdm-enrichment.enrichOne.MainEnrichProperty$.dlqTopic=dev_bevents_udds_mdm_rate_case68_uaspdto_dlq

mdm-enrichment.enrichOne.CommonEnrichProperty$.fromTopic=dev_input_converter_cardfl_refill_uasp
mdm-enrichment.enrichOne.CommonEnrichProperty$.dlqTopic=dev_input_converter_cardfl_refill_uasp_dlq

mdm-enrichment.enrichOne.CommonEnrichProperty$.keySelectorMain.isId=true
mdm-enrichment.enrichOne.CommonEnrichProperty$.keySelectorEnrich.isId=true
mdm-enrichment.enrichOne.CommonEnrichProperty$.inputDataFormat=UaspDtoFormat

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a1.fromFieldName=CrtXzFdTpCd
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a1.fromFieldType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a1.toFieldName=CrtXzFdTpCd
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a1.isOptionalEnrichValue=true

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a2.fromFieldName=CrtXzKkTpCd
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a2.fromFieldType=Int
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a2.toFieldName=CrtXzKkTpCd
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a2.isOptionalEnrichValue=true

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a3.fromFieldName=ZpBs144Flg
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a3.fromFieldType=Boolean
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a3.toFieldName=ZpBs144Flg
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a3.isOptionalEnrichValue=true

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a4.fromFieldName=mdmid
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a4.fromFieldType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a4.toFieldName=mdmid_cardfl
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a4.isOptionalEnrichValue=true

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

