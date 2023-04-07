package ru.vtb.uasp.inputconvertor.dao.dto

import play.api.libs.json.{Json, OWrites, Reads}
import ru.vtb.uasp.inputconvertor.dao.CommonDao.dtStringToLong

case class UddsUaspDto(
                        OPERATION_ID: String,
                        MDM_ID: String,
                        EVENT_DTTM: String,
                        OPERATION_AMOUNT: BigDecimal,
                        GOAL_CURRENCY: String,
                        TRADING_PLATFORM: String,
                        BALANCE_BEFORE_OPERATION: String,
                        INTERACTION_CHANNEL: String,
                        OPERATION_TYPE: String,
                        SOURCE_ACCOUNT: String,
                        SOURCE_ACCOUNT_BIC: String,
                        SOURCE_ACCOUNT_TYPE: String,
                        TARGET_ACCOUNT: String,
                        TARGET_ACCOUNT_BIC: String,
                        TARGET_ACCOUNT_TYPE: String,
                        CREDIT_TRANSACTION_ID: String,
                      ) {
  val eventDttmLong: Long = dtStringToLong(EVENT_DTTM, "yyyy-MM-dd HH:mm:ss", "GMT+0000")
}

object UddsUaspDto {
  implicit val reads: Reads[UddsUaspDto] = Json.reads[UddsUaspDto]
  implicit val writes: OWrites[UddsUaspDto] = Json.writes[UddsUaspDto]

}