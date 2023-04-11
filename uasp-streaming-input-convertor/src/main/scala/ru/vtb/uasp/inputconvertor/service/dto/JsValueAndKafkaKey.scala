package ru.vtb.uasp.inputconvertor.service.dto

import play.api.libs.json.{JsValue, Json, OWrites, Reads}

case class JsValueAndKafkaKey(kafkaKey: String,
                              uaspDto: JsValue
                          )

object JsValueAndKafkaKey {
  implicit val reads: Reads[JsValueAndKafkaKey] = Json.reads[JsValueAndKafkaKey]
  implicit val writes: OWrites[JsValueAndKafkaKey] = Json.writes[JsValueAndKafkaKey]

}