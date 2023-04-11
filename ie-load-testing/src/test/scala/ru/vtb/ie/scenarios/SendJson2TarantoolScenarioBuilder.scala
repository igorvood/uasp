package ru.vtb.ie.scenarios

import com.github.mnogu.gatling.kafka.Predef.kafka
import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import ru.tinkoff.gatling.feeders._
import ru.vtb.ie.config.Config
import ru.vtb.ie.generate.json.abstraction.AbstractStringIdentyfyedEntity
import ru.vtb.ie.inf.Scenario
import ru.vtb.ie.scenarios.UserIds.{ids, localCustomerId}


case class SendJson2TarantoolScenarioBuilder(config: Config,
                                             //                                              userCount: Int,
                                             //                                              countSeconds: Int,
                                             jsonGenerator: AbstractStringIdentyfyedEntity
                                            ) extends Scenario[String] {

  val ids2feeder: IndexedSeq[Map[String, String]] = ids(config.userCount * config.countSeconds).toFeeder(localCustomerId)

  private val userIdBytesFieldName = "userIdBytes"
  private val messageFieldName = "message"

  override def scenarioBuilder(scenarioName: String): ScenarioBuilder = {

    val sendScenario: ScenarioBuilder = scenario(scenarioName) feed ids2feeder

    sendScenario
      .exec { session => {
        val localUserId = session(localCustomerId).as[String]
        val userIdBytes = localUserId.getBytes
        if (config.isPrintRqBody)
          println(jsonGenerator.entityName + " -> " + jsonGenerator.jsonValue(localUserId))

        val updateSession = session
          .set(userIdBytesFieldName, userIdBytes)
          .set(messageFieldName, jsonGenerator.jsonValueAsBytes(localUserId))
        updateSession
      }
      }
      .exec {
        kafka(jsonGenerator.entityName).send[Array[Byte], Array[Byte]](s"$${$userIdBytesFieldName}", s"$${$messageFieldName}")
      }

  }

}
