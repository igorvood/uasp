package ru.vtb.uasp.inputconvertor.dao.dto

import play.api.libs.json.{Json, OWrites, Reads}

case class IssuingCardUaspDto(
                               id: String,
                               client: Client
                             )

case class Client(id: String)

object Client {
  implicit val reads: Reads[Client] = Json.reads[Client]
  implicit val writes: OWrites[Client] = Json.writes[Client]
}

object IssuingCardUaspDto {
  implicit val reads: Reads[IssuingCardUaspDto] = Json.reads[IssuingCardUaspDto]
  implicit val writes: OWrites[IssuingCardUaspDto] = Json.writes[IssuingCardUaspDto]
}