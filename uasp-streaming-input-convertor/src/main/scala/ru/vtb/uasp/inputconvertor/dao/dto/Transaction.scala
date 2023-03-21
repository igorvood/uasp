package ru.vtb.uasp.inputconvertor.dao.dto

import play.api.libs.json.{Json, OWrites, Reads}

case class Transaction(transactionId: String)

object Transaction {

  implicit val uaspJsonReads: Reads[Transaction] = Json.reads[Transaction]
  implicit val uaspJsonWrites: OWrites[Transaction] = Json.writes[Transaction]

}
