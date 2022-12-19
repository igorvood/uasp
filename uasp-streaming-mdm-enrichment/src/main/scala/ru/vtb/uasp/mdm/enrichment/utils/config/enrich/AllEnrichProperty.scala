package ru.vtb.uasp.mdm.enrichment.utils.config.enrich

import ru.vtb.uasp.common.utils.config.PropertyUtil.{createByClass, createByClassOption}
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, PropertyCombiner, ReadConfigErrors}

case class AllEnrichProperty(commonEnrichProperty: Option[CommonEnrichProperty],
                             globalIdEnrichProperty: Option[GlobalIdEnrichProperty],
                             mainEnrichProperty: MainEnrichProperty
                            ) {

  require(mainEnrichProperty != null, "MainEnrichProperty must be exactly one")

  require(globalIdEnrichProperty != null, "GlobalIdEnrichProperty must be one or none")

}

object AllEnrichProperty extends PropertyCombiner[AllEnrichProperty] {

  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, AllEnrichProperty] =
    for {
      mainEnrichProperty <- createByClass(prf, MainEnrichProperty.getClass, { p => MainEnrichProperty.create(p) })
      nonIdEnrichProperty <- createByClassOption(prf, CommonEnrichProperty.getClass, { p =>
        CommonEnrichProperty.create(p)
      })
      globalIdEnrichProperty <- createByClassOption(prf, GlobalIdEnrichProperty.getClass, { p => GlobalIdEnrichProperty.create(p) })
    } yield AllEnrichProperty(nonIdEnrichProperty, globalIdEnrichProperty, mainEnrichProperty)

}
