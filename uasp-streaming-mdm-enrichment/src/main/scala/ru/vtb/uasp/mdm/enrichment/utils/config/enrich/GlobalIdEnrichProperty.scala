package ru.vtb.uasp.mdm.enrichment.utils.config.enrich

import play.api.libs.json.JsValue
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.kafka.{FlinkConsumerProperties, FlinkSinkProperties}
import ru.vtb.uasp.common.utils.config.PropertyUtil.{createByClassOption, mapProperty, propertyVal, propertyValOptional}
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, PropertyCombiner, ReadConfigErrors}
import ru.vtb.uasp.mdm.enrichment.dao.UaspDtoPredef.PreDef
import ru.vtb.uasp.mdm.enrichment.service.dto.KeyedCAData
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf.InputFormatEnum._
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf._

case class GlobalIdEnrichProperty(
                                   fromTopic: FlinkConsumerProperties,
                                   dlqTopicProp: Option[FlinkSinkProperties],
                                   globalEnrichFields: EnrichFields,
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


  lazy val globalFields: EnrichPropertyFields = EnrichPropertyFieldsTemp(keySelectorMain, keySelectorEnrich, List(globalEnrichFields))


  //  override lazy val flatProperty: NodeJsonMeta = allFields.flatProperty


  override def validateFieldsAndExtractData(uaspDto: UaspDto): Either[String, KeyedCAData] = {
    for {
      noId <- super.validateFieldsAndExtractData(uaspDto)
      id <- uaspDto.getValueFromMapS(globalEnrichFields.fromFieldType, globalEnrichFields.fromFieldName).map(i => Right(i.toString)).getOrElse(Left(s"Not found new id for Uasp field name ${globalEnrichFields.fromFieldName} field type ${globalEnrichFields.fromFieldType}"))
    } yield noId.copy(newId = Some(id))
  }

  override def validateFieldsAndExtractData(jsValue: JsValue): Either[String, KeyedCAData] = {
    for {
      noId <- super.validateFieldsAndExtractData(jsValue)
      id <- globalFields.validateFieldsAndExtractData(jsValue).map(m => m.data.toList.head._2)
    } yield noId.copy(newId = Some(id))


  }

  private case class EnrichPropertyFieldsTemp(
                                               keySelectorMain: KeySelectorProp,
                                               keySelectorEnrich: KeySelectorProp,
                                               fields: List[EnrichFields]
                                             ) extends EnrichPropertyFields {
    override val isDeletedFieldPath: List[String] = List()
    //    override val flatProperty: NodeJsonMeta = NodeJsonMeta(fields.map(f => f.fromFieldName -> f.fromFieldType.toUpperCase()).toMap)
  }
}


object GlobalIdEnrichProperty extends PropertyCombiner[GlobalIdEnrichProperty] {


  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, GlobalIdEnrichProperty] = {
    val fieldsList = mapProperty(prf + ".fieldsList", { (str, appProps, ci) => EnrichFields(str)(appProps, ci) })
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
      globalEnrichFields <- EnrichFields.create(s"$prf.globalEnrichFields")

      keySelectorMain <- KeySelectorProp.create(s"$prf.keySelectorMain")
      keySelectorEnrich <- KeySelectorProp.create(s"$prf.keySelectorEnrich")
      fields <- fieldsList
      format <- propertyVal[InputFormatEnum](prf, "inputDataFormat")(appProps, configurationInitialise, { str => InputFormatEnum.withName(str) })
      isDeletedFieldPath <- propertyValOptional[String](prf, "isDeletedFieldPath").map(prp => prp.map(p => p.split("\\.").toList).getOrElse(List.empty))

    } yield new GlobalIdEnrichProperty(fromTopic, dlqTopicProp, globalEnrichFields, keySelectorMain, keySelectorEnrich, fields, format, isDeletedFieldPath)
  }
}
