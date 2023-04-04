package ru.vtb.uasp.mdm.enrichment.utils.config.enrich

import ru.vtb.uasp.common.kafka.{FlinkConsumerProperties, FlinkSinkProperties}
import ru.vtb.uasp.common.utils.config.PropertyUtil.{createByClassOption, mapProperty, propertyVal, propertyValOptional}
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, PropertyCombiner, ReadConfigErrors}
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf.InputFormatEnum._
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf.{InputFormatEnum, _}

/**
 * @param fromTopic          настройки топика с информацией по обогащению
 * @param dlqTopicProp       опциональный dlq топик для ошибок
 * @param keySelectorMain    правило для определения ключа у основного потока
 * @param keySelectorEnrich  правило для определения ключа у потока с обогащаемой информацией
 * @param fields             список полей с правилами обогащения
 * @param inputDataFormat    входной формат FlatJsonFormat или UaspDtoFormat
 * @param isDeletedFieldPath путь к полю с признаком удаления
 */
case class CommonEnrichProperty(
                                 fromTopic: FlinkConsumerProperties,
                                 dlqTopicProp: Option[FlinkSinkProperties],
                                 keySelectorMain: KeySelectorProp,
                                 keySelectorEnrich: KeySelectorProp,
                                 fields: List[EnrichFields],
                                 inputDataFormat: InputFormatEnum,
                                 isDeletedFieldPath: List[String]
                               ) extends EnrichProperty with EnrichPropertyWithDlq with EnrichPropertyFields with FormatSwitcher {

  require((inputDataFormat == FlatJsonFormat && keySelectorEnrich.isId == false) || inputDataFormat == UaspDtoFormat, s"for inputDataFormat = $inputDataFormat keySelectorEnrich.isId must be only false")
  require(
    inputDataFormat == FlatJsonFormat ||
      (inputDataFormat == UaspDtoFormat && (isDeletedFieldPath.isEmpty || isDeletedFieldPath.size == 1)),
    s"for $inputDataFormat isDeletedFieldPath must be empty or size equals 1, but $isDeletedFieldPath")
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
      isDeletedFieldPath <- propertyValOptional[String](prf, "isDeletedFieldPath").map(prp => prp.map(p => p.split("\\.").toList).getOrElse(List.empty))

    } yield new CommonEnrichProperty(
      fromTopic = fromTopic,
      dlqTopicProp = dlqTopicProp,
      keySelectorMain = keySelectorMain,
      keySelectorEnrich = keySelectorEnrich,
      fields = fields,
      inputDataFormat = format,
      isDeletedFieldPath = isDeletedFieldPath
    )
  }
}
