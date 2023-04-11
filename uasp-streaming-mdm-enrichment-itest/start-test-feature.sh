export prefix=itest-feature-45-
export COUNT_USERS=1000
export FIRST_USER_ID=100
export FIRST_USER_ID_GLOBAL=10000

export COUNT_TRANSACTION=1000
export TIME_WAIT_MAX=2800


export topicInWay4=dev_feature_ivr__uasp_realtime__input_converter__way4_issuing_operation__uaspdto
export topicInCrossLinkMdm=dev_feature_ivr__uasp_realtime__input_converter__mdm_cross_link__uaspdto
export topicOutCrossLinkMdmStatus=dev_feature_ivr__uasp_realtime__mdm_enrichment__mdm_cross_link__status
export topicOutEnrichmentWay4=dev_feature_ivr__uasp_realtime__input_converter__way4_issuing_operation__uaspdto
export topicDLQ=dev_feature_ivr__uasp_realtime__mdm_enrichment__for_additional_enrichment__uaspdto

export bootstrapServers="d5uasp-apc002lk.corp.dev.vtb:9092,d5uasp-apc003lk.corp.dev.vtb:9092,d5uasp-apc004lk.corp.dev.vtb:9092"
export groupIdCrossLinkStatusMdm=groupId-feature-mdm-enrichment-5
export groupIdWay4=consumer-groupId-feature-mdm-enrichment-way4-5
export groupIdDlq=consumer-groupId-feature-mdm-enrichment-way4-dlq-5
export sslTruststorePassword=${KAFKA_PASS}
export sslKeystorePassword=${KAFKA_PASS}
export sslKeyPassword=${KAFKA_PASS}

export sslTruststoreLocation="/opt/kafka-certs/kafka-trust.pfx"
export sslKeystoreLocation="/opt/kafka-certs/APD00.13.01-USBP-kafka-cluster-uasp.pfx"



java -Xmx10g -cp "./target/uasp-streaming-mdm-enrichment-itest-1.0-SNAPSHOT.jar:./target/uasp-streaming-mdm-enrichment-itest-1.0-SNAPSHOT-tests.jar:./target/original-uasp-streaming-mdm-enrichment-itest-1.0-SNAPSHOT.jar" io.gatling.app.Gatling -s ru.vtb.uasp.streaming.mdm.enrichment.itest.tests.UaspStreamingMdmEnrichmentITestScript
