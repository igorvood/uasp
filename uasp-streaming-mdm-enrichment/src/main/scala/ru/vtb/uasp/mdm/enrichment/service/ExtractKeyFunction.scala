package ru.vtb.uasp.mdm.enrichment.service


import org.apache.flink.api.scala.createTypeInformation
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import ru.vtb.uasp.common.abstraction.DlqProcessFunction
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.service.JsonConvertOutService.JsonPredef
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors}
import ru.vtb.uasp.mdm.enrichment.dao.JsonPredef.PreDef
import ru.vtb.uasp.mdm.enrichment.service.dto.KeyedCAData
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf.InputFormatEnum._
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf.{EnrichProperty, EnrichPropertyFields, EnrichPropertyWithDlq, FormatSwitcher}


class ExtractKeyFunction(
                          val enrichProperty: EnrichProperty with EnrichPropertyWithDlq with EnrichPropertyFields with FormatSwitcher
                        ) extends DlqProcessFunction[JsValue, KeyedCAData, KafkaDto] {

  override def processWithDlq(jsValue: JsValue): Either[KafkaDto, KeyedCAData] = {

    val mayBeValidDataState: Either[String, KeyedCAData] = enrichProperty.inputDataFormat match {
      case FlatJsonFormat => enrichProperty.validateFieldsAndExtractData(jsValue)
      case UaspDtoFormat =>
        jsValue.validate[UaspDto] match {
          case JsSuccess(uaspDto, _) => enrichProperty.validateFieldsAndExtractData(uaspDto)
          case JsError(errors) => Left(errors.extractStringError())
        }
    }

    val dtoOrData = mayBeValidDataState match {
      case Right(value) => Right(value)
      case Left(value) => Left(OutDtoWithErrors(Json.stringify(jsValue), List(value)).serializeToBytes)
    }
    dtoOrData

  }
}
