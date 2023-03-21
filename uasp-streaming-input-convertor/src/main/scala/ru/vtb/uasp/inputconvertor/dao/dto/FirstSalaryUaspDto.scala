package ru.vtb.uasp.inputconvertor.dao.dto

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, toFunctorOps}
import play.api.libs.json.{Json, OWrites, Reads, __}
import ru.vtb.uasp.inputconvertor.dao.CommonDao.dtStringToLong

case class FirstSalaryUaspDto(
                               internalId: Option[String],
                               clientId: Option[String],
                               eventTime: Option[Long],
                               systemId: Option[String],
                               vidDep: Option[String],
                               vidDepType: Option[String],
                               vidDepName: Option[String],
                               firstAdd: Option[Boolean],
                               transactions: List[Transaction],
                               data: Option[DataObj],
                             )

case class DataObj(
                    operationAmount: Option[OperationAmount],
                    feeAmount: Option[FeeAmount],
                    transactionAmount: Option[TransactionAmount],
                    KBO: Option[String],
                    operationName: Option[String],
                    account: Option[String],
                    status: Option[String],
                    transactionDate: Option[String],
                    debet: Option[Boolean],

                  )

case class OperationAmount(
                            sum: Option[BigDecimal],
                            currency: Option[String]
                          )

case class FeeAmount(
                      sum: Option[BigDecimal],
                      currency: Option[String],


                    )

case class TransactionAmount(
                              sum: Option[BigDecimal],
                              currency: Option[String]
                            )

object FirstSalaryUaspDto {

  implicit val reads1: Reads[FirstSalaryUaspDto] =
    (
      (__ \ "internalId").readNullable[String] and
        (__ \ "clientId").readNullable[String] and
        (__ \ "eventTime").readNullable[String].fmap(q => q.map(w => dtStringToLong(w, "yyyy-MM-dd'T'HH:mm:ss", "Europe/Moscow"))) and
        (__ \ "systemId").readNullable[String] and
        (__ \ "vidDep").readNullable[String] and
        (__ \ "vidDepType").readNullable[String] and
        (__ \ "vidDepName").readNullable[String] and
        (__ \ "firstAdd").readNullable[Boolean] and
        (__ \ "transactions").read[List[Transaction]] and
        (__ \ "data").readNullable[DataObj]
      ) (FirstSalaryUaspDto.apply _)

  implicit val writes: OWrites[FirstSalaryUaspDto] = Json.writes[FirstSalaryUaspDto]
}

object DataObj {
  implicit val reads: Reads[DataObj] = Json.reads[DataObj]
  implicit val writes: OWrites[DataObj] = Json.writes[DataObj]
}

object OperationAmount {
  implicit val reads: Reads[OperationAmount] = Json.reads[OperationAmount]
  implicit val writes: OWrites[OperationAmount] = Json.writes[OperationAmount]
}

object FeeAmount {
  implicit val reads: Reads[FeeAmount] = Json.reads[FeeAmount]
  implicit val writes: OWrites[FeeAmount] = Json.writes[FeeAmount]
}

object TransactionAmount {
  implicit val reads: Reads[TransactionAmount] = Json.reads[TransactionAmount]
  implicit val writes: OWrites[TransactionAmount] = Json.writes[TransactionAmount]
}
