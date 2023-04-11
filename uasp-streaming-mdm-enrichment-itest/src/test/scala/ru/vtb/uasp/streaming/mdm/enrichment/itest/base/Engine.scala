package ru.vtb.uasp.streaming.mdm.enrichment.itest.base

import io.gatling.app.Gatling
import io.gatling.core.config.GatlingPropertiesBuilder

object Engine extends App {

  val props = new GatlingPropertiesBuilder()
    .resourcesDirectory(IDEPathHelper.mavenResourcesDirectory.toString)
    .resultsDirectory(IDEPathHelper.resultsDirectory.toString)
    .binariesDirectory(IDEPathHelper.mavenBinariesDirectory.toString)
    .simulationClass("ru.vtb.uasp.streaming.mdm.enrichment.itest.tests.UaspStreamingMdmEnrichmentITestScript")

  Gatling.fromMap(props.build)
}
