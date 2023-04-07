package ru.vtb.uasp.inputconvertor.dao.dto

import play.api.libs.json.{Json, OWrites, Reads}

case class CADepositFlUaspDto(

                               mdm_id: String,
                               account_num: String,
                               product_nm: String,
                               product_rate: BigDecimal,
                               period: Int,
                             )

object CADepositFlUaspDto {
  implicit val reads: Reads[CADepositFlUaspDto] = Json.reads[CADepositFlUaspDto]
  implicit val writes: OWrites[CADepositFlUaspDto] = Json.writes[CADepositFlUaspDto]

}