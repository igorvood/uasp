mdm-enrichment.sync.parallelism=8

mdm-enrichment.service.name=mdm-enrichment-prof-tx-case-71
mdm-enrichment.enrichOne.MainEnrichProperty$.cns.group.id=mdm-enrichment-prof-tx-case-71_3



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

mdm-enrichment.producer.property.transactional.id=enrichment_transactional_id_case_71

mdm-enrichment.enrichOne.MainEnrichProperty$.fromTopic=dev_bevents__realtime__input_converter__prof__transactions__uaspdto
mdm-enrichment.enrichOne.MainEnrichProperty$.toTopicName=dev_bevents__realtime__case_71__uaspdto
mdm-enrichment.enrichOne.MainEnrichProperty$.dlqTopic=dev_bevents__realtime__case_71__uaspdto_dlq

mdm-enrichment.enrichOne.CommonEnrichProperty$.fromTopic=dev_rto_batch_ca_deposit_account_case_71_json_converted
mdm-enrichment.enrichOne.CommonEnrichProperty$.dlqTopic=dev_rto_batch_ca_deposit_account_case_71_uaspdto__dlq
mdm-enrichment.enrichOne.CommonEnrichProperty$.keySelectorMain.isId=false
mdm-enrichment.enrichOne.CommonEnrichProperty$.keySelectorMain.mapType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.keySelectorMain.mapKey=tcmt_account_num
mdm-enrichment.enrichOne.CommonEnrichProperty$.keySelectorEnrich.isId=true
mdm-enrichment.enrichOne.CommonEnrichProperty$.inputDataFormat=UaspDtoFormat

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a1.fromFieldName=product_nm
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a1.fromFieldType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a1.toFieldName=product_nm
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a1.isOptionalEnrichValue=true

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a2.fromFieldName=product_rate
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a2.fromFieldType=BigDecimal
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a2.toFieldName=product_rate
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a2.isOptionalEnrichValue=true

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a3.fromFieldName=period
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a3.fromFieldType=Int
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a3.toFieldName=period
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a3.isOptionalEnrichValue=true

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


