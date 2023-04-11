package ru.vtb.ie.inf

import io.gatling.core.structure.ScenarioBuilder

trait Scenario[T] {

  def scenarioBuilder(scenarioName: String): ScenarioBuilder

}
