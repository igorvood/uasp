package ru.vtb.uasp.common.service

import play.api.libs.json.{JsValue, Json, OWrites}
import ru.vtb.uasp.common.dto.Identity
import ru.vtb.uasp.common.mask.MaskedPredef.MaskJsValuePredef
import ru.vtb.uasp.common.mask.dto.{JsMaskedPath, JsMaskedPathError}
import ru.vtb.uasp.common.service.dto.{KafkaDto, KafkaJsValueDto, KafkaStrDto}

object JsonConvertOutService extends Serializable {

  def serializeToBytesIdentity[T <: Identity](value: T, maskedRule: Option[JsMaskedPath])(implicit oWrites: OWrites[T]): Either[List[JsMaskedPathError], KafkaDto] = {
    serializeToStr(value, maskedRule)
      .map(d => KafkaDto(d.id.getBytes(), d.value.getBytes()))
  }

  def serializeToKafkaJsValue[T <: Identity](value: T)(implicit oWrites: OWrites[T]): KafkaJsValueDto = KafkaJsValueDto(value.kafkaKey, Json.toJson(value))

  def serializeToBytes[T](value: T, maskedRule: Option[JsMaskedPath])(implicit oWrites: OWrites[T]): Either[List[JsMaskedPathError], KafkaDto] = {
    value.serializeToBytes(maskedRule)
  }


  def serializeToStr[T <: Identity](value: T, maskedRule: Option[JsMaskedPath])(implicit oWrites: OWrites[T]): Either[List[JsMaskedPathError], KafkaStrDto] = {
    serializeToStr(value.kafkaKey, value, maskedRule)
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

    private[common] def serializeToBytes(implicit oWrites: OWrites[T]): KafkaDto = JsonConvertOutService.serializeToBytesIdentity(self, None).right.get

    def serializeToKafkaJsValue(implicit oWrites: OWrites[T]): KafkaJsValueDto = KafkaJsValueDto(self.kafkaKey, Json.toJson(self))

    def serializeToBytes(maskedRule: Option[JsMaskedPath])(implicit oWrites: OWrites[T]): Either[List[JsMaskedPathError], KafkaDto] = JsonConvertOutService.serializeToBytesIdentity(self, maskedRule)

    def serializeToStr(maskedRule: Option[JsMaskedPath])(implicit oWrites: OWrites[T]): Either[List[JsMaskedPathError], KafkaStrDto] = JsonConvertOutService.serializeToStr(self, maskedRule)

  }

  implicit class JsonPredef[T](val self: T) extends AnyVal {

    private[common] def serializeToBytes(implicit oWrites: OWrites[T]): KafkaDto = {
      serializeToBytes(None).right.get
    }

    def serializeToKafkaJsValue(implicit oWrites: OWrites[T]): KafkaJsValueDto = KafkaJsValueDto(java.util.UUID.randomUUID().toString, Json.toJson(self))

    def serializeToBytes(maskedRule: Option[JsMaskedPath])(implicit oWrites: OWrites[T]): Either[List[JsMaskedPathError], KafkaDto] =
      serializeToBytes(java.util.UUID.randomUUID().toString, maskedRule)

    def serializeToBytes(id: String, maskedRule: Option[JsMaskedPath])(implicit oWrites: OWrites[T]): Either[List[JsMaskedPathError], KafkaDto] = {
      JsonConvertOutService.serializeToStr(id, self, maskedRule).map(v => KafkaDto(
        id = v.id.getBytes(),
        value = v.value.getBytes()
      ))

    }

  }

  implicit class JsonValuePredef(val self: JsValue) extends AnyVal {


    def serializeToBytes(id: String): KafkaDto = KafkaDto(id.getBytes(), Json.stringify(self).getBytes())


  }

}
