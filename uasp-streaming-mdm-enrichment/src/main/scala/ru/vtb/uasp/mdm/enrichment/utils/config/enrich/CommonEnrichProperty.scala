package ru.vtb.uasp.mdm.enrichment.utils.config.enrich

import ru.vtb.uasp.common.kafka.{FlinkConsumerProperties, FlinkSinkProperties}
import ru.vtb.uasp.common.utils.config.PropertyUtil.{createByClassOption, mapProperty, propertyVal}
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, PropertyCombiner, ReadConfigErrors}
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf.InputFormatEnum._
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf.{InputFormatEnum, _}

case class CommonEnrichProperty(
                                 fromTopic: FlinkConsumerProperties,
                                 dlqTopicProp: Option[FlinkSinkProperties],
                                 keySelectorMain: KeySelectorProp,
                                 keySelectorEnrich: KeySelectorProp,
                                 fields: List[EnrichFields],
                                 inputDataFormat: InputFormatEnum
                               ) extends EnrichProperty with EnrichPropertyWithDlq with EnrichPropertyFields with FormatSwitcher {
  override val isDeletedFieldPath: List[String] = List()
  require((inputDataFormat == FlatJsonFormat && keySelectorEnrich.isId == false) || inputDataFormat == UaspDtoFormat, s"for inputDataFormat = $inputDataFormat keySelectorEnrich.isId must be only false")
  //  lazy val flatProperty: NodeJsonMeta = NodeJsonMeta(fields.map(f => f.fromFieldName -> f.fromFieldType.toUpperCase() ).toMap)
}


object CommonEnrichProperty extends PropertyCombiner[CommonEnrichProperty] {


  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, CommonEnrichProperty] = {
    val stringToFields = mapProperty(prf + ".fieldsList", { (str, appProps, ci) => EnrichFields(str)(appProps, ci) })

    val fieldsList = stringToFields
      .map { d =>
        d.toList
          .sortBy(_._1)
          .map(_._2)
      }

    for {
      fromTopic <- FlinkConsumerProperties.create(prf)
      dlqTopicProp <- createByClassOption(s"$prf.dlq", FlinkSinkProperties.getClass, { p =>
        FlinkSinkProperties.create(p)
      })
      keySelectorMain <- KeySelectorProp.create(s"$prf.keySelectorMain")
      keySelectorEnrich <- KeySelectorProp.create(s"$prf.keySelectorEnrich")
      fields <- fieldsList
      format <- propertyVal[InputFormatEnum](prf, "inputDataFormat")(appProps, configurationInitialise, { str => InputFormatEnum.withName(str) })

    } yield new CommonEnrichProperty(
      fromTopic = fromTopic,
      dlqTopicProp = dlqTopicProp,
      keySelectorMain = keySelectorMain,
      keySelectorEnrich = keySelectorEnrich,
      fields = fields,
      inputDataFormat = format
    )
  }
}
