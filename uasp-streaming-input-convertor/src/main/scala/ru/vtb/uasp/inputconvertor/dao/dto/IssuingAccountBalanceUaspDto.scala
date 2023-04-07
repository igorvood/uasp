package ru.vtb.uasp.inputconvertor.dao.dto

import play.api.libs.json.{Json, OWrites, Reads}

case class IssuingAccountBalanceUaspDto(
                                         id: String
                                       )

object IssuingAccountBalanceUaspDto {
  implicit val reads: Reads[IssuingAccountBalanceUaspDto] = Json.reads[IssuingAccountBalanceUaspDto]
  implicit val writes: OWrites[IssuingAccountBalanceUaspDto] = Json.writes[IssuingAccountBalanceUaspDto]
}