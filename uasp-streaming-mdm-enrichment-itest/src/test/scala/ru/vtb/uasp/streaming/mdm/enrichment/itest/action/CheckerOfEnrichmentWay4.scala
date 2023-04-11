package ru.vtb.uasp.streaming.mdm.enrichment.itest.action

import io.gatling.core.session.Session
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.streaming.mdm.enrichment.itest.entity.CheckData
import ru.vtb.uasp.streaming.mdm.enrichment.itest.scenario.SendWay4ScenarioBuilder.{globalUserIdSessionName, _}
import ru.vtb.uasp.streaming.mdm.enrichment.itest.service.ConsumerService

class CheckerOfEnrichmentWay4(consumerService: ConsumerService[Array[Byte], Array[Byte], String, UaspDto, Map[String, UaspDto]]) extends Checker {

  override def check(session: Session): CheckData = {
    val globalUserId = session(globalUserIdSessionName).as[String]
    val clusterEnrichmentWay4UaspDtoMap = consumerService.getAll(globalUserId)

    val localEnrichmentWay4UaspDtoMap = session(localEnrichmentWay4UaspDtoMapSessionName).as[Map[String, UaspDto]]

    val (checkStatus, checkMsg) = if (localEnrichmentWay4UaspDtoMap.size != clusterEnrichmentWay4UaspDtoMap.size) {
      false -> s"count messages not equals local size ${localEnrichmentWay4UaspDtoMap.size.toString} cluster size: ${clusterEnrichmentWay4UaspDtoMap.size}"
    } else {
      equalsLocalwithClusterMessages(localEnrichmentWay4UaspDtoMap, clusterEnrichmentWay4UaspDtoMap)
    }

    CheckData(checkStatus, checkMsg)
  }

  private def equalsLocalwithClusterMessages(localMap: Map[String, UaspDto], clusterMap: Map[String, UaspDto]):  (Boolean, String) = {
    var checkStatus: Boolean = true
    var checkMsg: String = ""

    localMap.keys.foreach(key => {
      val local = localMap.get(key)
      val cluster = clusterMap.get(key)

      if (local.isDefined && cluster.isDefined) {
        if (!local.get.copy(uuid = "", process_timestamp = 0)
          .equals(cluster.get.copy(uuid = "", process_timestamp = 0))) {
          checkStatus = false
          checkMsg = "Messages not equals"
        }
      } else {
        checkStatus = false
        checkMsg = "Some messgaes is null"
      }
    })

    (checkStatus, checkMsg)
  }

}
