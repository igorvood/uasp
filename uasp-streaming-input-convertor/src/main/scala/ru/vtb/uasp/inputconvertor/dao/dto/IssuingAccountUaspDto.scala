package ru.vtb.uasp.inputconvertor.dao.dto

import play.api.libs.json.{Json, OWrites, Reads}

case class IssuingAccountUaspDto(
                                  id: String
                                )

object IssuingAccountUaspDto {
  implicit val reads: Reads[IssuingAccountUaspDto] = Json.reads[IssuingAccountUaspDto]
  implicit val writes: OWrites[IssuingAccountUaspDto] = Json.writes[IssuingAccountUaspDto]
}