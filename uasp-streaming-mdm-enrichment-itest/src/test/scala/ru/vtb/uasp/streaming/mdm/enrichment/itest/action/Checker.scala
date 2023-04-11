package ru.vtb.uasp.streaming.mdm.enrichment.itest.action

import io.gatling.core.session.Session
import ru.vtb.uasp.streaming.mdm.enrichment.itest.entity.CheckData

trait Checker {
  def check(session: Session): CheckData
}
