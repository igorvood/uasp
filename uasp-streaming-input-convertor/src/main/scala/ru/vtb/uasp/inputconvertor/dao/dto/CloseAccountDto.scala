package ru.vtb.uasp.inputconvertor.dao.dto

import play.api.libs.json.{Json, OWrites, Reads}

case class CloseAccountDto(
                            mdmCode: String,
                            productAS: String,
                            initAS: String,
                            dateApp: String,
                          )
object CloseAccountDto {
  implicit val reads: Reads[CloseAccountDto] = Json.reads[CloseAccountDto]
  implicit val writes: OWrites[CloseAccountDto] = Json.writes[CloseAccountDto]
}