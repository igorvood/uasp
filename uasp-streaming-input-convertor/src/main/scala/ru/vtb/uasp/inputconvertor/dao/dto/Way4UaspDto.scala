package ru.vtb.uasp.inputconvertor.dao.dto

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, toFunctorOps}
import play.api.libs.json.{Json, OWrites, Reads, __}
import ru.vtb.uasp.inputconvertor.dao.CommonDao.dtStringToLong

case class Way4UaspDto(
                        id: Option[String],
                        comment: Option[String],
                        account: Option[AccountDto],
                        chain: Option[ChainDto],
                        card: Option[CardDto],
                        actionType: Option[String],
                        requestedAmount: Option[RequestedAmountDto],
                        processing: Option[ProcessingDto],
                        pointOfService: Option[PointOfServiceDto],
                        counterpartyPaymentDetails: Option[CounterpartyPaymentDetailsDto],
                        taggedData: Option[TaggedDataDto],
                      )


case class AccountDto(accountNumber: Option[String],
                      client: Option[ClientDto]
                     )

case class ClientDto(id: Option[String])

case class ChainDto(ref: Option[RefDto],
                    serviceType: Option[String],
                    serviceDateTime: Option[Long],
                    serviceTypeExtension: Option[String]
                   )

case class RefDto(authCode: Option[String],
                  rrn: Option[String],
                  srn: Option[String],
                 )

case class CardDto(maskedPan: Option[String],
                   cardType: Option[CardTypeDto],
                   plastic: Option[PlasticDto]
                  )

case class CardTypeDto(psFundingSource: Option[String],
                       paymentScheme: Option[String],
                      )

case class PlasticDto(expire: Option[String])

case class RequestedAmountDto(paymentDirection: Option[String],
                              transaction: Option[TransactionDto]
                             )

case class TransactionDto(amount: Option[BigDecimal],
                          currency: Option[String]
                         )

case class ProcessingDto(effectiveDate: Option[Long],
                         resolution: Option[String],
                         resultCode: Option[String],
                         processedAt: Option[Long],
                         baseAmount: Option[BaseAmountDto],
                         feeAmount: Option[FeeAmountDto],
                        )

case class BaseAmountDto(
                          amount: Option[BigDecimal],
                          currency: Option[String]
                        )

case class FeeAmountDto(
                         amount: Option[BigDecimal],
                         currency: Option[String]

                       )

case class PointOfServiceDto(mcc: Option[String],
                             terminalType: Option[String],
                             merchantName: Option[String],
                             terminalId: Option[String],
                            )

case class CounterpartyPaymentDetailsDto(party: Option[PartyDto])

case class PartyDto(accountInfo: Option[AccountInfoDto])

case class AccountInfoDto(accountNumber: Option[String])

case class TaggedDataDto(KBO: Option[String],
                         SOURCE_PAY: Option[String],
                         WALLET_TYPE: Option[String],
                         ORIG_SRC_CODE: Option[String],
                        )

trait RW[T] {
  implicit val reads: Reads[T]
  implicit val writes: OWrites[T]
}

object Way4UaspDto extends RW[Way4UaspDto] {

  override implicit val reads: Reads[Way4UaspDto] = Json.reads[Way4UaspDto]
  override implicit val writes: OWrites[Way4UaspDto] = Json.writes[Way4UaspDto]
}

object AccountDto {
  implicit val reads: Reads[AccountDto] = Json.reads[AccountDto]
  implicit val writes: OWrites[AccountDto] = Json.writes[AccountDto]
}

object ClientDto {
  implicit val reads: Reads[ClientDto] = Json.reads[ClientDto]
  implicit val writes: OWrites[ClientDto] = Json.writes[ClientDto]
}

object ChainDto {

  implicit val reads: Reads[ChainDto] = (
    (__ \ "ref").readNullable[RefDto] and
      (__ \ "serviceType").readNullable[String] and
      (__ \ "serviceDateTime").readNullable[String].fmap(q => q.map(w => dtStringToLong(w, "yyyy-MM-dd'T'HH:mm:ss", "GMT+0000"))) and
      (__ \ "serviceTypeExtension").readNullable[String]
    ) (ChainDto.apply _)

  implicit val writes: OWrites[ChainDto] = Json.writes[ChainDto]
}

object RefDto {
  implicit val reads: Reads[RefDto] = Json.reads[RefDto]
  implicit val writes: OWrites[RefDto] = Json.writes[RefDto]
}

object CardDto {
  implicit val reads: Reads[CardDto] = Json.reads[CardDto]
  implicit val writes: OWrites[CardDto] = Json.writes[CardDto]
}

object CardTypeDto {
  implicit val reads: Reads[CardTypeDto] = Json.reads[CardTypeDto]
  implicit val writes: OWrites[CardTypeDto] = Json.writes[CardTypeDto]
}

object PlasticDto {
  implicit val reads: Reads[PlasticDto] = Json.reads[PlasticDto]
  implicit val writes: OWrites[PlasticDto] = Json.writes[PlasticDto]
}

object RequestedAmountDto {
  implicit val reads: Reads[RequestedAmountDto] = Json.reads[RequestedAmountDto]
  implicit val writes: OWrites[RequestedAmountDto] = Json.writes[RequestedAmountDto]
}

object TransactionDto {
  implicit val reads: Reads[TransactionDto] = Json.reads[TransactionDto]
  implicit val writes: OWrites[TransactionDto] = Json.writes[TransactionDto]
}

object ProcessingDto {

  implicit val reads: Reads[ProcessingDto] =
    (
      (__ \ "effectiveDate").readNullable[String].fmap(q => q.map(w => dtStringToLong(w, "yyyy-MM-dd", "GMT+0000"))) and
        (__ \ "resolution").readNullable[String] and
        (__ \ "resultCode").readNullable[String] and
        (__ \ "processedAt").readNullable[String].fmap(q => q.map(w => dtStringToLong(w, "yyyy-MM-dd'T'HH:mm:ss", "GMT+0000"))) and
        (__ \ "baseAmount").readNullable[BaseAmountDto] and
        (__ \ "feeAmount").readNullable[FeeAmountDto]
      ) (ProcessingDto.apply _)
  implicit val writes: OWrites[ProcessingDto] = Json.writes[ProcessingDto]
}

object BaseAmountDto {
  implicit val reads: Reads[BaseAmountDto] = Json.reads[BaseAmountDto]
  implicit val writes: OWrites[BaseAmountDto] = Json.writes[BaseAmountDto]
}

object FeeAmountDto {
  implicit val reads: Reads[FeeAmountDto] = Json.reads[FeeAmountDto]
  implicit val writes: OWrites[FeeAmountDto] = Json.writes[FeeAmountDto]
}

object PointOfServiceDto {
  implicit val reads: Reads[PointOfServiceDto] = Json.reads[PointOfServiceDto]
  implicit val writes: OWrites[PointOfServiceDto] = Json.writes[PointOfServiceDto]
}

object CounterpartyPaymentDetailsDto {
  implicit val reads: Reads[CounterpartyPaymentDetailsDto] = Json.reads[CounterpartyPaymentDetailsDto]
  implicit val writes: OWrites[CounterpartyPaymentDetailsDto] = Json.writes[CounterpartyPaymentDetailsDto]
}

object PartyDto {
  implicit val reads: Reads[PartyDto] = Json.reads[PartyDto]
  implicit val writes: OWrites[PartyDto] = Json.writes[PartyDto]
}

object AccountInfoDto {
  implicit val reads: Reads[AccountInfoDto] = Json.reads[AccountInfoDto]
  implicit val writes: OWrites[AccountInfoDto] = Json.writes[AccountInfoDto]
}

object TaggedDataDto {
  implicit val reads: Reads[TaggedDataDto] = Json.reads[TaggedDataDto]
  implicit val writes: OWrites[TaggedDataDto] = Json.writes[TaggedDataDto]
}
