package ru.vtb.uasp.mdm.enrichment.service

import org.apache.flink.streaming.api.scala.createTypeInformation
import play.api.libs.json.JsValue
import ru.vtb.uasp.common.abstraction.DlqProcessFunction
import ru.vtb.uasp.common.mask.dto.JsMaskedPathError
import ru.vtb.uasp.common.service.JsonConvertInService
import ru.vtb.uasp.common.service.dto.ServiceDataDto

class JsValueConsumer(val serviceDataDto: ServiceDataDto) extends DlqProcessFunction[Array[Byte], JsValue, JsMaskedPathError] {

  override def processWithDlq(dto: Array[Byte]): Either[JsMaskedPathError, JsValue] = {
    val value = JsonConvertInService.extractJsValue[JsMaskedPathError](dto)(serviceDataDto)
    val dtoOrValue = value match {
      case Right(v) => Right(v)
      case Left(value) => Left(JsMaskedPathError(value.errors.mkString("\n")))
    }
    dtoOrValue
  }


}
