package ru.vtb.ie.test

import com.github.mnogu.gatling.kafka.protocol.KafkaProtocol
import io.gatling.core.Predef._
import io.gatling.core.protocol.Protocol
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.{PopulationBuilder, ScenarioBuilder}
import io.gatling.http.Predef.http
import io.gatling.http.protocol.HttpProtocolBuilder
import ru.vtb.ie.config.{Config, ConfigUtil, KafkaPropertiesUtil}
import ru.vtb.ie.generate.json.datamodel.{BigJsonMeta, IntegrationElementJsonMeta, SmallJsonMeta}
import ru.vtb.ie.inf.KafkaConfiguration
import ru.vtb.ie.scenarios.{IntegrationElementScenarioBuilder, SendJson2TarantoolScenarioBuilder, TarantoolCheckScenarioBuilder}

import java.util.concurrent.TimeUnit
import scala.concurrent.duration.FiniteDuration

class UaspTarantoolScript extends Simulation with KafkaConfiguration[Config] {

  override def getKafkaCfg(sysEnv: Map[String, String]): Config = ConfigUtil.getConf(sysEnv)

  private val sendBigJsonTarantoolScenarioBuilder =
    SendJson2TarantoolScenarioBuilder(config, BigJsonMeta("big_json"))
      .scenarioBuilder("tarantool_big")

  private val sendSmallJsonTarantoolScenarioBuilder =
    SendJson2TarantoolScenarioBuilder(config, SmallJsonMeta("small_json"))
      .scenarioBuilder("tarantool_small")

  private val integrationElementScenarioBuilder =
    IntegrationElementScenarioBuilder(config, IntegrationElementJsonMeta("IE"))
      .scenarioBuilder("IE")

  private val tarantoolCheckScenarioBuilderDuration = TarantoolCheckScenarioBuilder(config, "$[0].cursor")
  private val tarantoolCheckScenarioBuilderCustomerAggComplete = TarantoolCheckScenarioBuilder(config, "$[0].cstmr_consent_bki_end_dt_diff_days")


  private val kafkaProtocolBigJson = new KafkaProtocol(config.topicBigJson, KafkaPropertiesUtil.getProducerKafkaProperties(config))
  private val kafkaProtocolSmallJson = new KafkaProtocol(config.topicSmallJson, KafkaPropertiesUtil.getProducerKafkaProperties(config))

  private val duration: FiniteDuration = FiniteDuration(config.countSeconds, TimeUnit.SECONDS)

  val populationBuilder: (ScenarioBuilder, Protocol) => PopulationBuilder = { (sc, protocol) =>
    sc
      .inject(constantUsersPerSec(config.userCount).during(duration))
      .protocols(protocol)
  }

  val httpIeProtocol: HttpProtocolBuilder = http
    .baseUrl(config.baseUrlIE)
    .acceptHeader("application/json")

  val httpTarantoolProtocol: HttpProtocolBuilder = http
    .baseUrl(config.baseUrlTarantool)
    .acceptHeader("application/json")
    .header("Authorization", "Bearer 5ecfcd97-7457-4ad8-bc30-6f1329eac681")


  private val integrationElementReq: PopulationBuilder = integrationElementScenarioBuilder
    .inject(constantUsersPerSec(config.userCount).during(duration))
    .protocols(httpIeProtocol)

  private val smallJson: PopulationBuilder =
    if (config.checkTarantoolRest)
      populationBuilder(sendSmallJsonTarantoolScenarioBuilder, kafkaProtocolSmallJson)
        .andThen(populationBuilder(tarantoolCheckScenarioBuilderDuration.scenarioBuilder("Duration"), httpTarantoolProtocol))
    else populationBuilder(sendSmallJsonTarantoolScenarioBuilder, kafkaProtocolSmallJson)

  private val bigJson: PopulationBuilder =
    if (config.checkTarantoolRest) populationBuilder(sendBigJsonTarantoolScenarioBuilder, kafkaProtocolBigJson)
      .andThen(populationBuilder(tarantoolCheckScenarioBuilderCustomerAggComplete.scenarioBuilder("CustomerAggComplete"), httpTarantoolProtocol))
    else populationBuilder(sendBigJsonTarantoolScenarioBuilder, kafkaProtocolBigJson)

  setUp(
    smallJson,
    bigJson
      .andThen(integrationElementReq)
  )

}
