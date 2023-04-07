package ru.vtb.uasp.inputconvertor.dao.dto

import play.api.libs.json.{Json, OWrites, Reads}
import ru.vtb.uasp.inputconvertor.dao.CommonDao.dtStringToLong
import ru.vtb.uasp.inputconvertor.dao.ProfileUaspDtoDao

case class ProfileUaspDto(
                           cid: String,
                           cdt: String,
                           tim: String,
                           spr: String,
                           drcr: Option[String],
                           tcmt: Option[String],
                           tso: Option[String],
                           bcrcd: Option[String],
                           ztsoatmc: Option[String],
                           prin: Option[BigDecimal],
                           bseamt: Option[BigDecimal],
                           crcd: Option[String],
                           ztsotrntype: Option[String],
                           endbal: Option[BigDecimal],
                           SHA_CARD_NUMBER_DKO: Option[String],
                         ) {
  val tcmt_account_num: Option[String] = tcmt.map {
    tcmt =>
      if (tcmt.contains("Перенос начисленных процентов согласно условиям договора. Вклад") &&
        ztsotrntype.exists(_.equals("731720-00")))
        "\\d{20}".r.findFirstMatchIn(tcmt).map(_.toString()).getOrElse("********************")
      else "********************"
  }
  val data_transactionDate: String = cdt + " " + tim
  val eventTime: Long = dtStringToLong(cdt + "T" + tim, "yyyy-MM-dd'T'HHmmss", ProfileUaspDtoDao.timeZone)
}

object ProfileUaspDto {
  implicit val reads: Reads[ProfileUaspDto] = Json.reads[ProfileUaspDto]
  implicit val writes: OWrites[ProfileUaspDto] = Json.writes[ProfileUaspDto]

}
