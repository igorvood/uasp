package ru.vtb.uasp.inputconvertor.service.dto

import play.api.libs.json.{JsValue, Json, OWrites, Reads}
import ru.vtb.uasp.common.dto.UaspDto

case class UaspAndKafkaKey(kafkaKey: String,
                           uaspDto: JsValue
                          )

object UaspAndKafkaKey {
  implicit val reads: Reads[UaspAndKafkaKey] = Json.reads[UaspAndKafkaKey]
  implicit val writes: OWrites[UaspAndKafkaKey] = Json.writes[UaspAndKafkaKey]

}