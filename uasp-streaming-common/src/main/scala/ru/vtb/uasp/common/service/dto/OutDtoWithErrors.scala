package ru.vtb.uasp.common.service.dto

import play.api.libs.json.{Json, OWrites, Reads}

case class OutDtoWithErrors(sourceValue: String,
                            errors: List[String]
                                         )

object OutDtoWithErrors {
  implicit val reads: Reads[OutDtoWithErrors] = Json.reads[OutDtoWithErrors]
  implicit val write: OWrites[OutDtoWithErrors] = Json.writes[OutDtoWithErrors]

}