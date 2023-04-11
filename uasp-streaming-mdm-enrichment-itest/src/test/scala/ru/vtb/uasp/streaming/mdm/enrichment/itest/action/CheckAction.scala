package ru.vtb.uasp.streaming.mdm.enrichment.itest.action

import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.session.Session
import io.gatling.core.structure.ScenarioContext
import ru.vtb.uasp.streaming.mdm.enrichment.itest.entity.CheckData

class CheckAction(nameAction: String, checker: Checker) extends ActionBuilder {
  override def build(ctx: ScenarioContext, nextAction: Action): Action =
    new CheckChainableAction(nameAction, nextAction, ctx, checker)
}

class CheckChainableAction(nameAction: String, nextAction: Action, ctx: ScenarioContext, checker: Checker)
  extends ChainableAction {
  var startTime: Long = System.currentTimeMillis()
  var stopTime: Long = System.currentTimeMillis()

  override def next: Action = nextAction

  override def execute(session: Session): Unit = {
    try {
      val checkData: CheckData = checker.check(session)

      ctx.coreComponents.statsEngine.logResponse(
        session.scenario,
        session.groups,
        requestName = name,
        startTimestamp = startTime,
        endTimestamp = stopTime,
        status = if (checkData.checkStatus) OK else KO,
        None,
        message = if (checkData.checkStatus) None else Option(checkData.checkMsg),
      )

      nextAction ! session
    } catch {
      case e: Exception =>
        val stopTime = System.currentTimeMillis()

        ctx.coreComponents.statsEngine.logResponse(
          session.scenario,
          session.groups,
          requestName = name,
          startTimestamp = startTime,
          endTimestamp = stopTime,
          status = KO,
          None,
          Some(e.getMessage)
        )

        nextAction ! session
    }

  }

  override def name: String = nameAction
}