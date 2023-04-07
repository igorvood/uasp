package ru.vtb.uasp.inputconvertor.dao.dto

import play.api.libs.json.{Json, OWrites, Reads}
import ru.vtb.uasp.inputconvertor.dao.CommonDao.dtStringToLong
import ru.vtb.uasp.inputconvertor.dao.ProfileAuthUaspDtoDao
import ru.vtb.uasp.inputconvertor.utils.CurrencyConverter.returnAlphaCode

import java.time.LocalDate
import java.util.Calendar

case class ProfileAuthUaspDto(
                               msgid: String,
                               card_number: String,
                               SHA_CARD_NUMBER_DKO: Option[String],
                               sha_card_number: Option[String],
                               transaction_amt: String,
                               transaction_currency_cd: String,
                               typ: String,
                               local_transaction_dt: String,
                               local_transaction_ts: String,
                               merchant_category_cd: String,
                               transmission_dttm: String,
                               replacement_amt: String,
                               response_code: String,
                               terminal_class: String,
                               transaction_cd: String,
                               terminal_owner: String,
                               message_type: String,
                               card_acceptor_terminal_ident: String,

                             ) {

  val transaction_amt_big_decimal: BigDecimal = BigDecimal(transaction_amt)
  val transaction_currency_cd_decimal: Int = transaction_currency_cd.toInt
  val typ_decimal: Int = typ.toInt

  val currency_alpha_code: String = returnAlphaCode(transaction_currency_cd)

  val currentYear: Int = Calendar.getInstance.get(Calendar.YEAR)

  ////Вычиляем корректность даты, если входящая дата + currentYear больше чем текущая дата то берем предыдущий год для даты
  val calculatedYear: Int = if (LocalDate.parse(currentYear.toString + "-" + transmission_dttm.substring(0, 2) + "-" + transmission_dttm.substring(2, 4)).isAfter(LocalDate.now())) {
    currentYear - 1
  } else currentYear

  val eventTime: Long = dtStringToLong(calculatedYear.toString + local_transaction_dt + "T" + local_transaction_ts, "yyyyMMdd'T'HHmmss", ProfileAuthUaspDtoDao.timeZone)
  val transmission_dttm_formatted: Long = dtStringToLong(calculatedYear.toString + transmission_dttm, "yyyyMMddHHmmss", ProfileAuthUaspDtoDao.timeZone)

}


object ProfileAuthUaspDto {
  implicit val reads: Reads[ProfileAuthUaspDto] = Json.reads[ProfileAuthUaspDto]
  implicit val writes: OWrites[ProfileAuthUaspDto] = Json.writes[ProfileAuthUaspDto]


}