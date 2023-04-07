package ru.vtb.uasp.inputconvertor.dao.dto

import play.api.libs.json.{Json, OWrites, Reads}

case class CustomerIdProfileFullUaspDto(
                                         contract_id: String,
                                         customer_id: String,
                                         contract_num: Option[String],
                                       )

object CustomerIdProfileFullUaspDto {
  implicit val reads: Reads[CustomerIdProfileFullUaspDto] = Json.reads[CustomerIdProfileFullUaspDto]
  implicit val writes: OWrites[CustomerIdProfileFullUaspDto] = Json.writes[CustomerIdProfileFullUaspDto]
}
