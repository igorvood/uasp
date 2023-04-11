package ru.vtb.ie.scenarios

import io.gatling.commons.validation.Success
import io.gatling.core.Predef._
import io.gatling.core.check.CheckBuilder
import io.gatling.core.check.string.BodyStringCheckType
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import ru.tinkoff.gatling.feeders._
import ru.vtb.ie.config.Config
import ru.vtb.ie.inf.Scenario
import ru.vtb.ie.scenarios.UserIds.{ids, localCustomerId}

import scala.language.postfixOps


case class TarantoolCheckScenarioBuilder(config: Config,
                                         //                                         userCount: Int,
                                         //                                         countSeconds: Int,
                                         notEmptyField: String
                                         //                                          jsonGenerator: AbstractStringIdentyfyedEntity
                                        ) extends Scenario[String] {

  val ids2feeder: IndexedSeq[Map[String, String]] = ids(config.userCount * config.countSeconds).toFeeder(localCustomerId)

  private val customer_id = "customer_id"

  override def scenarioBuilder(scenarioName: String): ScenarioBuilder = {
    val sendScenario: ScenarioBuilder = scenario(scenarioName) feed ids2feeder

    val value: CheckBuilder[BodyStringCheckType, String, String] = bodyString.saveAs("BODY")
    sendScenario
      .exec(session => {
        val localUserId = session(localCustomerId).as[String]
        //      val localUserId = "586233333"
        //      val localUserId = "586233333123"
        val updateSession = session
          .set(customer_id, localUserId)
        updateSession
      }
      ).exec(http(scenarioName)

      .get("/data/" + scenarioName + "/?customer_id=${" + customer_id + "}")

      .check(
        bodyString.saveAs("BODY")
      )
      //      .check(jsonPath(notEmptyField) exists)
      //      .check(responseTimeInMillis.lte(500))
    ).exec { session =>
      if (config.isPrintSession) {
        println("Tarantool=>" + session)
      }
      session
    }
      .doIf(session => Success[Boolean](config.isPrintRspTarantoolBody)) {
        exec(session => {
          val response = session("BODY").as[String]
          println(s"Response body $scenarioName: $response")
          session
        })

      }
  }
}
