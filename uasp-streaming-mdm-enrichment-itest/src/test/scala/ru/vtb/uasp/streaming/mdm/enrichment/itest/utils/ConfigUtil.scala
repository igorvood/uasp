package ru.vtb.uasp.streaming.mdm.enrichment.itest.utils

import ru.vtb.uasp.streaming.mdm.enrichment.itest.entity.Config

object ConfigUtil {

  val groupId = 23

  def getConf(sysEnv: Map[String, String]): Config = {
    val config: Config = Config(
      topicInWay4 = sysEnv.getOrElse("topicInWay4", "dev_ivr__uasp_realtime__input_converter__way4_issuing_operation__uaspdto"),
      topicInCrossLinkMdm = sysEnv.getOrElse("topicInCrossLinkMdm", "dev_ivr__uasp_realtime__input_converter__mdm_cross_link__uaspdto"),
      topicInMortgage = sysEnv.getOrElse("topicInMortgage", "dev_ivr__uasp_realtime__input_converter__mortgage__uaspdto"),
      topicOutMortgage = sysEnv.getOrElse("topicOutMortgage", "dev_ivr__uasp_realtime__input_converter__mortgage__status"),
      topicOutCrossLinkMdmStatus = sysEnv.getOrElse("topicOutCrossLinkMdmStatus", "dev_ivr__uasp_realtime__mdm_enrichment__mdm_cross_link__status"),
      topicOutEnrichmentWay4 = sysEnv.getOrElse("topicOutEnrichmentWay4", "dev_ivr__uasp_realtime__mdm_enrichment__uaspdto"),
      topicDLQ = sysEnv.getOrElse("topicDLQ", "dev_ivr__uasp_realtime__mdm_enrichment__for_additional_enrichment__uaspdto"),
      topicInRate = sysEnv.getOrElse("topicInRate", "rate_in"),
      topicOutRate = sysEnv.getOrElse("topicInRate", "rate_out"),
      bootstrapServers = sysEnv.getOrElse("bootstrapServers", "172.17.69.246:9092,172.17.69.246:9091"),
      groupIdCrossLinkStatusMdm = sysEnv.getOrElse("groupIdCrossLinkStatusMdm", "groupIdCrossLinkStatusMdm_ksu"),
      groupIdMortgageMdm = sysEnv.getOrElse("groupIdMortgageMdm", "groupIdMortgageMdm-" + groupId),
      groupIdWay4 = sysEnv.getOrElse("groupIdWay4", "groupIdWay4_ksu-" + groupId),
      groupIdDlq = sysEnv.getOrElse("groupIdDlq", "groupIdDlq_ksu-" + groupId),

      sslTruststoreLocation = sysEnv.getOrElse("sslTruststoreLocation", "C:\\Users\\ALogvinov\\Documents\\vtb-cloud\\dso\\certs\\kafka-trust.pfx"),
      sslTruststorePassword = sysEnv.getOrElse("sslTruststorePassword", "kafkauasppassword"),
      sslKeystoreLocation = sysEnv.getOrElse("sslKeystoreLocation", "C:\\Users\\ALogvinov\\Documents\\vtb-cloud\\dso\\certs\\APD00.13.01-USBP-kafka-cluster-uasp.pfx"),
      sslKeystorePassword = sysEnv.getOrElse("sslKeystorePassword", "kafkauasppassword"),
      sslKeyPassword = sysEnv.getOrElse("sslKeyPassword", "kafkauasppassword"),

      /**
       * sysEnv.getOrElse("sslTruststoreLocation", "kafka-trust.pfx"),
       * sysEnv.getOrElse("sslTruststorePassword", ""),
       * sysEnv.getOrElse("sslKeystoreLocation", "APD00.13.01-USBP-kafka-cluster-uasp.pfx"),
       * sysEnv.getOrElse("sslKeystorePassword", ""),
       * sysEnv.getOrElse("sslKeyPassword", ""), */

      nameStateDayHourAggregates = sys.env.getOrElse("nameStateDayHourAggregates", "DayHourAggregates"),
      nameStateMaxDurations = sys.env.getOrElse("nameStateMaxDurations", "MaxDurations"),
      nameStateLastTransaction = sys.env.getOrElse("nameStateLastTransaction", "LastTransaction"),
      enablePrefix = sys.env.getOrElse("enablePrefix", "true"),
      prefix = sys.env.getOrElse("prefix", "EnItest!-"),
      rateList = sys.env.getOrElse("prefix", "USD,EUR,RUB").split(",").toList,
      dateDiapason = sys.env.getOrElse("prefix", "1").toInt,
    )

    config
  }
}
