package ru.vtb.uasp.streaming.mdm.enrichment.itest.action

import io.gatling.core.session.Session
import ru.vtb.uasp.mdm.enrichment.service.rate.dto.RateResponse
import ru.vtb.uasp.mdm.enrichment.service.rate.dto.ResultRate.{ERROR, SUCCESS}
import ru.vtb.uasp.streaming.mdm.enrichment.itest.entity.CheckData
import ru.vtb.uasp.streaming.mdm.enrichment.itest.scenario.SendRateScenarioBuilder.rateIdStrSessionName
import ru.vtb.uasp.streaming.mdm.enrichment.itest.service.ConsumerServiceNew


class CheckerOfEnrichmentRate(service: ConsumerServiceNew[Array[Byte], Array[Byte], String, RateResponse, Seq[RateResponse], String]) extends Checker {
  override def check(session: Session): CheckData = {
    val rateId = session(rateIdStrSessionName).as[String]
    val response = service.get(rateId)
    response.result match {
      case SUCCESS => CheckData(checkStatus = true, "")
      case ERROR => CheckData(checkStatus = false, "result must be SUCCESS")
    }

  }
}
