package ru.vtb.uasp.inputconvertor.dao.dto

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, toFunctorOps}
import play.api.libs.json.{Json, OWrites, Reads, __}
import ru.vtb.uasp.inputconvertor.dao.CommonDao.dtStringToLong

case class RatesDto(

                     id: String,
                     date: Long,
                     rates: List[RateDto]

                   )

case class RateDto(currency: CurrencyDto,
                   price: BigDecimal,
                   scale: Int
                  )

case class CurrencyDto(
                        name: String,
                        numericCode: String,
                        alphaCode: String,
                      )

object CurrencyDto {
  implicit val reads: Reads[CurrencyDto] = Json.reads[CurrencyDto]
  implicit val writes: OWrites[CurrencyDto] = Json.writes[CurrencyDto]

}

object RateDto {
  implicit val reads: Reads[RateDto] = Json.reads[RateDto]
  implicit val writes: OWrites[RateDto] = Json.writes[RateDto]

}

object RatesDto {

  implicit val reads: Reads[RatesDto] = (
    (__ \ "id").read[String] and
      (__ \ "date").read[String].fmap(q => dtStringToLong(q, "yyyy-MM-dd", "UTC")) and
      (__ \ "rates").read[List[RateDto]]
    ) (RatesDto.apply _)

  implicit val writes: OWrites[RatesDto] = Json.writes[RatesDto]

}