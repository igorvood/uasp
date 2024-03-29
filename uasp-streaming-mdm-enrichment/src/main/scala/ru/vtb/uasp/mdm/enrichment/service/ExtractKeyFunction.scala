package ru.vtb.uasp.mdm.enrichment.service


import org.apache.flink.api.scala.createTypeInformation
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{JsError, JsSuccess, JsValue}
import ru.vtb.uasp.common.abstraction.DlqProcessFunction
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.service.dto.{OutDtoWithErrors, ServiceDataDto}
import ru.vtb.uasp.mdm.enrichment.dao.JsonPredef.PreDef
import ru.vtb.uasp.mdm.enrichment.service.dto.KeyedCAData
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf.InputFormatEnum._
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf.{EnrichProperty, EnrichPropertyFields, EnrichPropertyWithDlq, FormatSwitcher}


class ExtractKeyFunction(val serviceDataDto: ServiceDataDto,
                         val enrichProperty: EnrichProperty with EnrichPropertyWithDlq with EnrichPropertyFields with FormatSwitcher
                        ) extends DlqProcessFunction[JsValue, KeyedCAData, OutDtoWithErrors[JsValue]] {

  override def processWithDlq(dto: JsValue): Either[OutDtoWithErrors[JsValue], KeyedCAData] = {

    val mayBeValidDataState = enrichProperty.inputDataFormat match {
      case FlatJsonFormat => enrichProperty.validateFieldsAndExtractData(dto)
      case UaspDtoFormat => dto.validate[UaspDto] match {
        case JsSuccess(uaspDto, _) => enrichProperty.validateFieldsAndExtractData(uaspDto)
        case JsError(errors) => Left(errors.extractStringError())
      }
    }

    mayBeValidDataState match {
      case Right(value) => Right(value)
      case Left(value) => Left(OutDtoWithErrors[JsValue](serviceDataDto, Some(this.getClass.getName), List(value), Some(dto)))
    }

  }
}
