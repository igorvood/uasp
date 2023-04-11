package ru.vtb.uasp.streaming.mdm.enrichment.itest.action

import io.gatling.core.session.Session
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.streaming.mdm.enrichment.itest.entity.CheckData
import ru.vtb.uasp.streaming.mdm.enrichment.itest.scenario.SendMortgageScenarioBuilder._

class CheckerOfMortgage extends Checker {

  override def check(session: Session): CheckData = {
    val localMortgageUaspDto = session(localMortgageUaspDtoSessionName).as[UaspDto]
    val clusterMortgageUaspDto = session(clusterMortgageUaspDtoSessionName).as[UaspDto]

    val (checkStatus: Boolean, checkMsg: String) =
      if (localMortgageUaspDto != null & clusterMortgageUaspDto != null) {
        if (!localMortgageUaspDto.equals(clusterMortgageUaspDto)) {
          (false, "")
        } else {
          (true, "")
        }
      }
      else {
        (false, "clusterModelVector is null")
      }

    CheckData(checkStatus, checkMsg)
  }

}
