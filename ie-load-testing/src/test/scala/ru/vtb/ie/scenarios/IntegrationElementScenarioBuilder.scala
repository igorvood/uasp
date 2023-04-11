package ru.vtb.ie.scenarios

import io.gatling.commons.validation.Success
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import ru.tinkoff.gatling.feeders._
import ru.vtb.ie.config.Config
import ru.vtb.ie.generate.json.abstraction.AbstractStringIdentyfyedEntity
import ru.vtb.ie.inf.Scenario
import ru.vtb.ie.scenarios.UserIds.{ids, localCustomerId}

import scala.language.postfixOps


case class IntegrationElementScenarioBuilder(config: Config,
                                             jsonGenerator: AbstractStringIdentyfyedEntity
                                            ) extends Scenario[String] {

  val ids2feeder: IndexedSeq[Map[String, String]] = ids(config.userCount * config.countSeconds).toFeeder(localCustomerId)

  private val customer_id = "customer_id"

  override def scenarioBuilder(scenarioName: String): ScenarioBuilder = {
    val sendScenario: ScenarioBuilder = scenario(scenarioName) feed ids2feeder

    val builder = http(jsonGenerator.entityName)
      .post("/api/v1/model")
      .header("Content-Type", "application/json")
    //    println(builder.toString)
    sendScenario
      .exec { session =>
        if (config.isPrintSession) {
          println("IE=>"+session)
        }
        session
      }
      .exec(session => {
        val localUserId = session(localCustomerId).as[String]
        val request = jsonGenerator.jsonValue(localUserId)
        //      val request = jsonGenerator.jsonValue("586233333")

        //      val request = jsonGenerator.jsonValue("1646292368493")
        //      println(jsonGenerator.entityName + " -> " + request)
        val updateSession = session
          .set(customer_id, request)
        updateSession
      }
      ).exec(
      builder

        .body(StringBody("${" + customer_id + "}")).asJson
        .check(bodyString.saveAs("BODY"))
        .check(jsonPath("$.customer_id") exists)
        .check(jsonPath("$.error_code") notExists)
    ).doIf(session => Success[Boolean](config.isPrintRspIEBody)) {
      exec(session => {
        val response = session("BODY").as[String]
        println(s"Response body $scenarioName: \n$response")
        session
      })
    }
  }
}
