package ru.vtb.uasp.common.service

import play.api.libs.json.{Json, OWrites}
import ru.vtb.uasp.common.dto.Identity
import ru.vtb.uasp.common.mask.MaskedPredef.MaskJsValuePredef
import ru.vtb.uasp.common.mask.dto.{JsMaskedPath, JsMaskedPathError}
import ru.vtb.uasp.common.service.dto.{KafkaDto, KafkaStrDto}

object JsonConvertOutService extends Serializable {

  def serializeToBytes[T <: Identity](value: T, maskedRule: Option[JsMaskedPath])(implicit oWrites: OWrites[T]): Either[List[JsMaskedPathError], KafkaDto] = {
    serializeToStr(value, maskedRule)
      .map(d => KafkaDto(d.id.getBytes(), d.value.getBytes()))
  }

  def serializeToStr[T <: Identity](value: T, maskedRule: Option[JsMaskedPath])(implicit oWrites: OWrites[T]): Either[List[JsMaskedPathError], KafkaStrDto] = {
    serializeToStr(value.id, value, maskedRule)
  }


  def serializeToStr[T](id: String, value: T, maskedRule: Option[JsMaskedPath])(implicit oWrites: OWrites[T]): Either[List[JsMaskedPathError], KafkaStrDto] = {
    val jsObject = Json.toJsObject(value)
    val maybeErrorsOrValue: Either[List[JsMaskedPathError], KafkaStrDto] = maskedRule
      .map { mr => {
        jsObject.toMaskedJson(mr)
          .map(jsMask => KafkaStrDto(id, Json.stringify(jsMask)))
      }
      }.getOrElse(Right(KafkaStrDto(id, Json.stringify(jsObject))))

    maybeErrorsOrValue
  }

  implicit class IdentityPredef[T <: Identity](val self: T) extends AnyVal {

    def serializeToBytes(maskedRule: Option[JsMaskedPath])(implicit oWrites: OWrites[T]): Either[List[JsMaskedPathError], KafkaDto] = JsonConvertOutService.serializeToBytes(self, maskedRule)

    def serializeToStr(maskedRule: Option[JsMaskedPath])(implicit oWrites: OWrites[T]): Either[List[JsMaskedPathError], KafkaStrDto] = JsonConvertOutService.serializeToStr(self, maskedRule)

  }

  implicit class JsonPredef[T](val self: T) extends AnyVal {

    def serializeToBytes( maskedRule: Option[JsMaskedPath])(implicit oWrites: OWrites[T]): Either[List[JsMaskedPathError], KafkaDto] =
      serializeToBytes(java.util.UUID.randomUUID().toString, maskedRule)

    def serializeToBytes(id: String, maskedRule: Option[JsMaskedPath])(implicit oWrites: OWrites[T]): Either[List[JsMaskedPathError], KafkaDto] = {
      JsonConvertOutService.serializeToStr(id, self, maskedRule).map(v => KafkaDto(
        id = v.id.getBytes(),
        value = v.value.getBytes()
      ))

    }

    def serializeToStr(maskedRule: Option[JsMaskedPath])(implicit oWrites: OWrites[T]): Either[List[JsMaskedPathError], String] = JsonConvertOutService.serializeToStr("", self, maskedRule).map(v =>v.value)

  }


}
