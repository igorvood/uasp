package ru.vtb.uasp.mdm.enrichment.utils.config.enrich

import ru.vtb.uasp.common.utils.config.PropertyUtil._
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, PropertyCombiner, ReadConfigErrors}
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.AppConst.mapsTypes

case class KeySelectorProp(
                            isId: Boolean,
                            mapType: Option[String] = None,
                            mapKey: Option[String] = None) {

  require(
    (isId && mapType.isEmpty && mapKey.isEmpty)
      ||
      !isId, "if isId equals true mapType and mapKey must be empty")

  require(
    (!isId && mapType.isDefined && mapKey.isDefined && mapType.get.nonEmpty && mapKey.get.nonEmpty)
      ||
      isId
    , "if isId equals false mapType and mapKey must be non empty")

  require((mapType.isDefined && mapsTypes.contains(mapType.get.toUpperCase())) || mapType.isEmpty, s"map type ${mapType.get} must contains in $mapsTypes ")

}


object KeySelectorProp extends PropertyCombiner[KeySelectorProp] {


  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, KeySelectorProp] =
    for {
      isId <- propertyVal[Boolean](prf, "isId")
      mapType <- propertyValOptional[String](prf, "mapType")(appProps, configurationInitialise, s)
      mapKey <- propertyValOptional[String](prf, "mapKey")(appProps, configurationInitialise, s)

    } yield KeySelectorProp(
      isId = isId,
      mapType = mapType,
      mapKey = mapKey,
    )
}