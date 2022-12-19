mdm-enrichment.sync.parallelism=8

mdm-enrichment.service.name=mdm-enrichment-way4-card-agreement
mdm-enrichment.enrichOne.MainEnrichProperty$.cns.group.id=mdm-enrichment-way4-card-agreement_3

#mdm-enrichment.enrichOne.MainEnrichProperty$.cns.bootstrap.servers=d5uasp-apc003lk.corp.dev.vtb:9092,d5uasp-apc003lk.corp.dev.vtb:9092,d5uasp-apc004lk.corp.dev.vtb:9092,d5uasp-apc018lk.corp.dev.vtb:9092,d5uasp-apc025lk.corp.dev.vtb:9092
#mdm-enrichment.enrichOne.MainEnrichProperty$.cns.security.protocol=SSL

mdm-enrichment.enrichOne.MainEnrichProperty$.cns.bootstrap.servers=172.20.219.153:9092,172.20.219.153:9091
mdm-enrichment.enrichOne.MainEnrichProperty$.cns.security.protocol=PLAINTEXT
mdm-enrichment.enrichOne.MainEnrichProperty$.cns.ssl.key.password=kafkauasppassword
mdm-enrichment.enrichOne.MainEnrichProperty$.cns.ssl.keystore.password=kafkauasppassword
mdm-enrichment.enrichOne.MainEnrichProperty$.cns.ssl.truststore.password=kafkauasppassword
mdm-enrichment.enrichOne.MainEnrichProperty$.cns.ssl.keystore.location=C:\\Work\\SSL\\APD00.13.01-USBP-kafka-cluster-uasp.pfx
mdm-enrichment.enrichOne.MainEnrichProperty$.cns.ssl.truststore.location=C:\\Work\\SSL\\kafka-trust.pfx

mdm-enrichment.enrichOne.MainEnrichProperty$.dlq.FlinkSinkProperties$.prd.bootstrap.servers=172.20.219.153:9092,172.20.219.153:9091
mdm-enrichment.enrichOne.MainEnrichProperty$.dlq.FlinkSinkProperties$.prd.security.protocol=PLAINTEXT
mdm-enrichment.enrichOne.MainEnrichProperty$.dlq.FlinkSinkProperties$.prd.ssl.key.password=kafkauasppassword
mdm-enrichment.enrichOne.MainEnrichProperty$.dlq.FlinkSinkProperties$.prd.ssl.keystore.password=kafkauasppassword
mdm-enrichment.enrichOne.MainEnrichProperty$.dlq.FlinkSinkProperties$.prd.ssl.truststore.password=kafkauasppassword
mdm-enrichment.enrichOne.MainEnrichProperty$.dlq.FlinkSinkProperties$.prd.ssl.keystore.location=C:\\Work\\SSL\\APD00.13.01-USBP-kafka-cluster-uasp.pfx
mdm-enrichment.enrichOne.MainEnrichProperty$.dlq.FlinkSinkProperties$.prd.ssl.truststore.location=C:\\Work\\SSL\\kafka-trust.pfx
mdm-enrichment.enrichOne.MainEnrichProperty$.dlq.FlinkSinkProperties$.prd.transactional.id=enrichment_transactional_id_way4
mdm-enrichment.enrichOne.MainEnrichProperty$.dlq.FlinkSinkProperties$.prd.ssl.keystore.type=PKCS12
mdm-enrichment.enrichOne.MainEnrichProperty$.dlq.FlinkSinkProperties$.prd.ssl.truststore.type=PKCS12
mdm-enrichment.enrichOne.MainEnrichProperty$.dlq.FlinkSinkProperties$.prd.transaction.timeout.ms=1800000
mdm-enrichment.enrichOne.MainEnrichProperty$.dlq.FlinkSinkProperties$.toTopic=dev_bevents_card_agreement_enrich_way4_dlq

mdm-enrichment.enrichOne.MainEnrichProperty$.out.FlinkSinkProperties$.prd.bootstrap.servers=172.20.219.153:9092,172.20.219.153:9091
mdm-enrichment.enrichOne.MainEnrichProperty$.out.FlinkSinkProperties$.prd.security.protocol=PLAINTEXT
mdm-enrichment.enrichOne.MainEnrichProperty$.out.FlinkSinkProperties$.prd.ssl.key.password=kafkauasppassword
mdm-enrichment.enrichOne.MainEnrichProperty$.out.FlinkSinkProperties$.prd.ssl.keystore.password=kafkauasppassword
mdm-enrichment.enrichOne.MainEnrichProperty$.out.FlinkSinkProperties$.prd.ssl.truststore.password=kafkauasppassword
mdm-enrichment.enrichOne.MainEnrichProperty$.out.FlinkSinkProperties$.prd.ssl.keystore.location=C:\\Work\\SSL\\APD00.13.01-USBP-kafka-cluster-uasp.pfx
mdm-enrichment.enrichOne.MainEnrichProperty$.out.FlinkSinkProperties$.prd.ssl.truststore.location=C:\\Work\\SSL\\kafka-trust.pfx
mdm-enrichment.enrichOne.MainEnrichProperty$.out.FlinkSinkProperties$.prd.transactional.id=enrichment_transactional_id_way4
mdm-enrichment.enrichOne.MainEnrichProperty$.out.FlinkSinkProperties$.prd.ssl.keystore.type=PKCS12
mdm-enrichment.enrichOne.MainEnrichProperty$.out.FlinkSinkProperties$.prd.ssl.truststore.type=PKCS12
mdm-enrichment.enrichOne.MainEnrichProperty$.out.FlinkSinkProperties$.prd.transaction.timeout.ms=1800000
mdm-enrichment.enrichOne.MainEnrichProperty$.out.FlinkSinkProperties$.toTopic=dev_bevents_card_agreement_enrich_out_uaspdto

mdm-enrichment.enrichOne.MainEnrichProperty$.fromTopic=dev_ivr__uasp_realtime__case_48_concatenate__uaspdto

mdm-enrichment.enrichOne.CommonEnrichProperty$.dlq.FlinkSinkProperties$.prd.bootstrap.servers=172.20.219.153:9092,172.20.219.153:9091
mdm-enrichment.enrichOne.CommonEnrichProperty$.dlq.FlinkSinkProperties$.prd.security.protocol=PLAINTEXT
mdm-enrichment.enrichOne.CommonEnrichProperty$.dlq.FlinkSinkProperties$.prd.ssl.key.password=kafkauasppassword
mdm-enrichment.enrichOne.CommonEnrichProperty$.dlq.FlinkSinkProperties$.prd.ssl.keystore.password=kafkauasppassword
mdm-enrichment.enrichOne.CommonEnrichProperty$.dlq.FlinkSinkProperties$.prd.ssl.truststore.password=kafkauasppassword
mdm-enrichment.enrichOne.CommonEnrichProperty$.dlq.FlinkSinkProperties$.prd.ssl.keystore.location=C:\\Work\\SSL\\APD00.13.01-USBP-kafka-cluster-uasp.pfx
mdm-enrichment.enrichOne.CommonEnrichProperty$.dlq.FlinkSinkProperties$.prd.ssl.truststore.location=C:\\Work\\SSL\\kafka-trust.pfx
mdm-enrichment.enrichOne.CommonEnrichProperty$.dlq.FlinkSinkProperties$.prd.transactional.id=enrichment_transactional_id_way4
mdm-enrichment.enrichOne.CommonEnrichProperty$.dlq.FlinkSinkProperties$.prd.ssl.keystore.type=PKCS12
mdm-enrichment.enrichOne.CommonEnrichProperty$.dlq.FlinkSinkProperties$.prd.ssl.truststore.type=PKCS12
mdm-enrichment.enrichOne.CommonEnrichProperty$.dlq.FlinkSinkProperties$.prd.transaction.timeout.ms=1800000
mdm-enrichment.enrichOne.CommonEnrichProperty$.dlq.FlinkSinkProperties$.toTopic=dev_rto_batch_ca_customer_card_uaspdto__dlq


mdm-enrichment.enrichOne.CommonEnrichProperty$.fromTopic=dev__dko_uasp__card_agreement_converted
mdm-enrichment.enrichOne.CommonEnrichProperty$.keySelectorMain.isId=false
mdm-enrichment.enrichOne.CommonEnrichProperty$.keySelectorMain.mapType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.keySelectorMain.mapKey=customer_id_and_masked_card_number
mdm-enrichment.enrichOne.CommonEnrichProperty$.keySelectorEnrich.isId=false
mdm-enrichment.enrichOne.CommonEnrichProperty$.keySelectorEnrich.mapType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.keySelectorEnrich.mapKey=customer_id_and_masked_card_number
mdm-enrichment.enrichOne.CommonEnrichProperty$.inputDataFormat=UaspDtoFormat

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a01.fromFieldName=mask_card_number
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a01.fromFieldType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a01.toFieldName=MASK_CARD_NUMBER
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a01.isOptionalEnrichValue=true

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a02.fromFieldName=customer_id
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a02.fromFieldType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a02.toFieldName=customer_id
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a02.isOptionalEnrichValue=true

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a03.fromFieldName=source_system_cd
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a03.fromFieldType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a03.toFieldName=source_system_cd
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a03.isOptionalEnrichValue=true

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a04.fromFieldName=pos_flg
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a04.fromFieldType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a04.toFieldName=pos_flg
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a04.isOptionalEnrichValue=true

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a05.fromFieldName=account_num
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a05.fromFieldType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a05.toFieldName=account_num
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a05.isOptionalEnrichValue=true

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a06.fromFieldName=is_virtual_card_flg
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a06.fromFieldType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a06.toFieldName=is_virtual_card_flg
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a06.isOptionalEnrichValue=true

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a07.fromFieldName=card_expiration_dt
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a07.fromFieldType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a07.toFieldName=card_expiration_dt
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a07.isOptionalEnrichValue=true

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a08.fromFieldName=payment_system_desc
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a08.fromFieldType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a08.toFieldName=payment_system_desc
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a08.isOptionalEnrichValue=true

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a09.fromFieldName=card_type_cd
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a09.fromFieldType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a09.toFieldName=card_type_cd
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a09.isOptionalEnrichValue=true

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a10.fromFieldName=salary_serv_pack_flg
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a10.fromFieldType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a10.toFieldName=salary_serv_pack_flg
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a10.isOptionalEnrichValue=true

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a11.fromFieldName=salary_project_flg
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a11.fromFieldType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a11.toFieldName=salary_project_flg
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a11.isOptionalEnrichValue=true

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a12.fromFieldName=salary_account_scheme_flg
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a12.fromFieldType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a12.toFieldName=salary_account_scheme_flg
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a12.isOptionalEnrichValue=true

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a13.fromFieldName=salary_card_type_flg
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a13.fromFieldType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a13.toFieldName=salary_card_type_flg
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a13.isOptionalEnrichValue=true

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a14.fromFieldName=contract_card_type_cd
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a14.fromFieldType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a14.toFieldName=contract_card_type_cd
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a14.isOptionalEnrichValue=true

mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a15.fromFieldName=hash_card_number
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a15.fromFieldType=String
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a15.toFieldName=hash_card_number
mdm-enrichment.enrichOne.CommonEnrichProperty$.fieldsList.a15.isOptionalEnrichValue=true

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

