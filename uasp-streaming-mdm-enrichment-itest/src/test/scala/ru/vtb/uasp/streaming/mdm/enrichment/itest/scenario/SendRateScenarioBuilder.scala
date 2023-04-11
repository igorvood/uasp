package ru.vtb.uasp.streaming.mdm.enrichment.itest.scenario

import com.github.mnogu.gatling.kafka.Predef.kafka
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import org.joda.time.DateTime
import play.api.libs.json.Json
import ru.vtb.uasp.mdm.enrichment.service.rate.dto.{Currency, Rate, RateResponse, Rates}
import ru.vtb.uasp.streaming.mdm.enrichment.itest.action.{CheckAction, CheckerOfEnrichmentRate}
import ru.vtb.uasp.streaming.mdm.enrichment.itest.common.{ConsumerRecordAccumulatorNew, FooCounter, OutTopicChecker}
import ru.vtb.uasp.streaming.mdm.enrichment.itest.dao.RateAccumulator
import ru.vtb.uasp.streaming.mdm.enrichment.itest.entity.Config
import ru.vtb.uasp.streaming.mdm.enrichment.itest.scenario.SendRateScenarioBuilder.{generateRates, rateIdSessionName, rateIdStrSessionName, ratesSessionName}
import ru.vtb.uasp.streaming.mdm.enrichment.itest.utils.IdsListGenerator.config
import ru.vtb.uasp.streaming.mdm.enrichment.itest.utils.KafkaPropertiesUtil

import java.util.{Date, Properties}

class SendRateScenarioBuilder(val config: Config
                             ) extends OutTopicChecker[Array[Byte], Array[Byte], String, RateResponse, Seq[RateResponse], String] {


  private val fooCounter = new FooCounter(0)

  override val consumerRecordAccumulator: ConsumerRecordAccumulatorNew[Array[Byte], Array[Byte], String, RateResponse, Seq[RateResponse], String] = new RateAccumulator()
  override val outTopicName: String = config.topicOutRate
  override val consumerKafkaProperties: Properties = KafkaPropertiesUtil.getConsumerKafkaProperties(config, "Rate " + new Date())

  def getScenario: ScenarioBuilder = {
    scenario("Rate scenario")
      .exec { session =>
        val index = fooCounter.inc()
        val rateId = config.evalPrefix + index
        val rates = generateRates(index.toInt)
        val jsObject = Json.toJsObject(rates)
        val ratesBytes = Json.stringify(jsObject).getBytes()
        session
          .set(rateIdSessionName, rateId.getBytes())
          .set(rateIdStrSessionName, rateId)
          .set(ratesSessionName, ratesBytes)
      }
      .exec(kafka("kafkaInRateMessagesConf").send[Array[Byte], Array[Byte]]("${" + rateIdSessionName + "}", "${" + ratesSessionName + "}"))
      .asLongAs(_ => consumerService.getCountMessages < config.dateDiapason, "CheckCountWay4Messages") {
        exec(session => session).pause(10)
      }
      .exec(new CheckAction("CheckerOfEnrichmentRate", new CheckerOfEnrichmentRate(consumerService)))
      .exec(session => {
        consumerService.finish()
        session
      })
  }
}

object SendRateScenarioBuilder {

  val rateIdSessionName = "rateId"
  val ratesSessionName = "rates"
  val rateIdStrSessionName = "rateIdStrSessionName"

  val rateList: List[String] = config.rateList
  val timeNow: DateTime = DateTime.now()

  def generateRates(index: Int) = {

    val ratesGen = rateList.map { rate =>
      val i = (rate.hashCode + index.hashCode()).abs % 100 + 1
      Rate(i, 1, Currency("Какая-то валюта", "111", rate))
    }
    Rates(config.evalPrefix + index.toString, timeNow.plusDays(index), ratesGen)
  }

}