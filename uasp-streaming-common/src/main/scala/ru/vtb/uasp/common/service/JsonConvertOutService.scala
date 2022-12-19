package ru.vtb.uasp.common.service

import play.api.libs.json.{Json, OWrites}
import ru.vtb.uasp.common.dto.Identity
import ru.vtb.uasp.common.service.dto.{KafkaDto, KafkaStrDto}

object JsonConvertOutService extends Serializable {

  def serializeToBytes[T <: Identity](value: T)(implicit oWrites: OWrites[T]): KafkaDto = {
    val kafkaStrDto = serializeToStr(value)
    KafkaDto(kafkaStrDto.id.getBytes(), kafkaStrDto.value.getBytes())
  }

  def serializeToStr[T <: Identity](value: T)(implicit oWrites: OWrites[T]): KafkaStrDto = KafkaStrDto(value.id, Json.stringify(Json.toJsObject(value)))


  implicit class IdentityPredef[T <: Identity](val self: T) extends AnyVal {

    def serializeToBytes(implicit oWrites: OWrites[T]): KafkaDto = JsonConvertOutService.serializeToBytes(self)

    def serializeToStr(implicit oWrites: OWrites[T]): KafkaStrDto = JsonConvertOutService.serializeToStr(self)

  }

  implicit class JsonPredef[T](val self: T) extends AnyVal {

    def serializeToBytes(implicit oWrites: OWrites[T]): KafkaDto =
      serializeToBytes(java.util.UUID.randomUUID().toString)

    def serializeToBytes(id: String)(implicit oWrites: OWrites[T]): KafkaDto =
      KafkaDto(
        id = id.getBytes(),
        value = Json.stringify(Json.toJsObject(self)).getBytes()
      )

    def serializeToStr(implicit oWrites: OWrites[T]): String = Json.stringify(Json.toJsObject(self))

  }


}
