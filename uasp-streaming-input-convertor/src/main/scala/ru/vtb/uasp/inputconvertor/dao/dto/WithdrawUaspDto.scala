package ru.vtb.uasp.inputconvertor.dao.dto

import play.api.libs.json.{Json, OWrites, Reads}

case class WithdrawUaspDto(
                            eventType: String,
                            operationCode: String,
                            transferOrderId: String,
                            senderMdmId: String,
                            private val updatedAt: Double,
                            fields: FieldsDto,
                          ) {
  val updatedAtMultiply: Double = updatedAt * 1000
}

case class FieldsDto(
                      sourceSumRub: BigDecimal,
                      targetMaskedPan: Option[String],
                      targetAccount: Option[String],
                      targetBankRussianName: Option[String],
                      sourceMaskedPan: Option[String],
                      sourceAccount: String,
                      receiverFpsBankId: Option[String],
                      receiverName: Option[String],
                      senderName: Option[String],
                      interactionChannel: Option[String],
                    )

object FieldsDto {
  implicit val reads: Reads[FieldsDto] = Json.reads[FieldsDto]
  implicit val writes: OWrites[FieldsDto] = Json.writes[FieldsDto]

}

object WithdrawUaspDto {
  implicit val reads: Reads[WithdrawUaspDto] = Json.reads[WithdrawUaspDto]
  implicit val writes: OWrites[WithdrawUaspDto] = Json.writes[WithdrawUaspDto]

}