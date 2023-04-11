package ru.vtb.uasp.streaming.mdm.enrichment.itest.tests

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import ru.vtb.uasp.streaming.mdm.enrichment.itest.scenario._
import ru.vtb.uasp.streaming.mdm.enrichment.itest.utils.IdsListGenerator.config.{kafkaInMdmCrossLinkMessagesConf, kafkaInMortgageMessagesConf, kafkaInWay4MessagesConf}
import ru.vtb.uasp.streaming.mdm.enrichment.itest.utils.IdsListGenerator.{COUNT_TRANSACTION, COUNT_USERS, config}

class UaspStreamingMdmEnrichmentITestScript extends Simulation {
  val COUNT_MESSAGES: Int = COUNT_USERS * COUNT_TRANSACTION
  val CASE_NUMBER: Int = sys.env.getOrElse("CASE_NUMBER", "1").toInt

//     setUp(new SendRateScenarioBuilder(config).getScenario.inject(atOnceUsers(config.dateDiapason)).protocols(config.kafkaInRateMessagesConf))

  if (CASE_NUMBER == 1) {
    // Передачи кросс ссылок мдм (наполняем состояние кросс ссылками перед стартом обогащения)
    val sendMdmCrossLinksScenarioBuilder: SendMdmCrossLinksScenarioBuilder = new SendMdmCrossLinksScenarioBuilder(COUNT_USERS, config)
    val sendMdmCrossLinksScenario = sendMdmCrossLinksScenarioBuilder.getSendMdmCrossLinksScenario

    val sendMortgageBuilder: SendMortgageScenarioBuilder = new SendMortgageScenarioBuilder(COUNT_USERS, config)
    val sendMortgageScenario = sendMortgageBuilder.getSendMortgageScenario

    val sendRateScenarioBuilder = new SendRateScenarioBuilder(config)

    // Отправка в Way4 сообщение дожидается завершения передачи кросс ссылок мдм
    val sendWay4ScenarioBuilder: SendWay4ScenarioBuilder = new SendWay4ScenarioBuilder(COUNT_USERS, COUNT_TRANSACTION, config, sendMdmCrossLinksScenarioBuilder, sendMortgageBuilder, sendRateScenarioBuilder)
    val sendWay4Scenario = sendWay4ScenarioBuilder.getSendWay4Scenario


    setUp(
      sendRateScenarioBuilder.getScenario.inject(atOnceUsers(config.dateDiapason)).protocols(config.kafkaInRateMessagesConf),
      sendMdmCrossLinksScenario.inject(atOnceUsers(COUNT_USERS)).protocols(kafkaInMdmCrossLinkMessagesConf),
      sendMortgageScenario.inject(atOnceUsers(COUNT_USERS)).protocols(kafkaInMortgageMessagesConf),
      sendWay4Scenario.inject(atOnceUsers(COUNT_USERS)).protocols(kafkaInWay4MessagesConf)
    )
  }
  else if (CASE_NUMBER == 2) {
    val sendOnlyWay4ScenarioBuilder: SendOnlyWay4ScenarioBuilder = new SendOnlyWay4ScenarioBuilder(COUNT_USERS, COUNT_TRANSACTION, config)
    val sendOnlyWay4Scenario = sendOnlyWay4ScenarioBuilder.getSendOnlyWay4Scenario

    setUp(
      sendOnlyWay4Scenario.inject(atOnceUsers(COUNT_USERS)).protocols(kafkaInWay4MessagesConf)
    )

  }

}
