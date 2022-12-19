package ru.vtb.uasp.mdm.enrichment.utils.config.enrich

import ru.vtb.uasp.common.utils.config.PropertyUtil._
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, PropertyCombiner, ReadConfigErrors}
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.AppConst.mapsTypes

case class EnrichFields(
                         fromFieldName: String,
                         fromFieldType: String,
                         toFieldName: String,
                         isOptionalEnrichValue: Boolean) {

  require(fromFieldName != null && fromFieldName.nonEmpty, "fromFieldName must be not empty")
  require(fromFieldType != null && fromFieldType.nonEmpty, "fromFieldType must be not empty")
  require(toFieldName != null && toFieldName.nonEmpty, "toFieldName must be not empty")

  require(mapsTypes.contains(fromFieldType.toUpperCase()), s"Type $fromFieldType must contains in $mapsTypes ")
}


object EnrichFields extends PropertyCombiner[EnrichFields] {

  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, EnrichFields] =
    for {
      fromFieldName <- propertyVal[String](prf, "fromFieldName")(appProps, configurationInitialise, s)
      fromFieldType <- propertyVal[String](prf, "fromFieldType")(appProps, configurationInitialise, s)
      toFieldName <- propertyVal[String](prf, "toFieldName")(appProps, configurationInitialise, s)
      isOptionalEnrichValue <- propertyVal[Boolean](prf, "isOptionalEnrichValue")

    } yield EnrichFields(
      fromFieldName = fromFieldName,
      fromFieldType = fromFieldType,
      toFieldName = toFieldName,
      isOptionalEnrichValue = isOptionalEnrichValue,
    )
}