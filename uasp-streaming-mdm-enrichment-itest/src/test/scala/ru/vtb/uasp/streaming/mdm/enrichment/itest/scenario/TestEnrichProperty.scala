package ru.vtb.uasp.streaming.mdm.enrichment.itest.scenario

import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.{CommonEnrichProperty, GlobalIdEnrichProperty}

object TestEnrichProperty {

  val globalIdEnrichPropertyTest = new GlobalIdEnrichProperty("unused", Some("unused"), None, "global_id", "String", "global_id", isOptionalEnrichValue = true, "way4_process_timestamp")

  val hypothecProperty = new CommonEnrichProperty("unused", Some("unused"), None, "is_mortgage", "Boolean", "is_mortgage", true)
}
