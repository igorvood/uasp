package ru.vtb.uasp.streaming.mdm.enrichment.itest.action

import io.gatling.core.session.Session
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.streaming.mdm.enrichment.itest.entity.CheckData
import ru.vtb.uasp.streaming.mdm.enrichment.itest.scenario.SendMdmCrossLinksScenarioBuilder._

class CheckerOfCrossLinkMdm extends Checker {

  override def check(session: Session): CheckData = {

    val localCrossLinkUaspDto = session(localCrossLinkUaspDtoSessionName).as[UaspDto]
    val clusterCrossLink = session(clusterСrossLinkUaspDtoSessionName).as[UaspDto]

    val (checkStatus, checkMsg) = if (localCrossLinkUaspDto != null & clusterCrossLink != null) {
      if (!localCrossLinkUaspDto.copy(process_timestamp = 0).equals(clusterCrossLink.copy(process_timestamp = 0))) {
        false -> None
      } else true -> None
    }
    else {
      false -> Some("clusterModelVector is null")
    }

    CheckData(checkStatus, checkMsg.getOrElse(""), System.currentTimeMillis(), System.currentTimeMillis())
  }

}
