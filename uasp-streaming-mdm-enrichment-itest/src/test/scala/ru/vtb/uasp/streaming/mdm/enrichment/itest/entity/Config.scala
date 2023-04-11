package ru.vtb.uasp.streaming.mdm.enrichment.itest.entity

import com.github.mnogu.gatling.kafka.Predef.kafka
import com.github.mnogu.gatling.kafka.protocol.KafkaProtocol
import io.gatling.core.Predef.configuration
import ru.vtb.uasp.streaming.mdm.enrichment.itest.utils.KafkaPropertiesUtil

import java.util.Date

case class Config(
                   topicInWay4: String,
                   topicInCrossLinkMdm: String,
                   topicInMortgage: String,
                   topicOutMortgage: String,
                   topicOutCrossLinkMdmStatus: String,
                   topicOutEnrichmentWay4: String,
                   topicDLQ: String,
                   topicInRate: String,
                   topicOutRate: String,
                   bootstrapServers: String,
                   groupIdCrossLinkStatusMdm: String,
                   groupIdMortgageMdm: String,
                   groupIdWay4: String,
                   groupIdDlq: String,
                   sslTruststoreLocation: String,
                   sslTruststorePassword: String,
                   sslKeystoreLocation: String,
                   sslKeystorePassword: String,
                   sslKeyPassword: String,
                   nameStateDayHourAggregates: String,
                   nameStateMaxDurations: String,
                   nameStateLastTransaction: String,
                   enablePrefix: String,
                   prefix: String,
                   rateList: List[String],
                   dateDiapason: Int
                 ){

  val evalPrefix: String = if (enablePrefix.equals("true")) s"$prefix${new Date()}=>" else new Date().toString

  val kafkaInMdmCrossLinkMessagesConf: KafkaProtocol =getKafkaProtocol(topicInCrossLinkMdm)

  val kafkaInMortgageMessagesConf: KafkaProtocol = getKafkaProtocol(topicInMortgage)

  val kafkaInRateMessagesConf: KafkaProtocol = getKafkaProtocol(topicInRate)

  val kafkaInWay4MessagesConf: KafkaProtocol = getKafkaProtocol(topicInWay4)


  private def getKafkaProtocol(topicName: String) : KafkaProtocol = kafka.topic(topicName)
    .properties(KafkaPropertiesUtil.getProducerKafkaProperties(this))

}
