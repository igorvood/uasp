package ru.vtb.uasp.inputconvertor.dao.dto

import play.api.libs.json.{Json, OWrites, Reads}

case class CAFirstSalaryUaspDto(
                                 mdm_id_ca: String,
                                 private val event_dttm_ca: Long,
                                 kbo_ca: String,
                                 trans_amount_ca: BigDecimal,
                                 source_system_ca: String,
                               ) {
  val event_dttm_ca_calc: Long = event_dttm_ca * 1000

}

object CAFirstSalaryUaspDto {
  implicit val reads: Reads[CAFirstSalaryUaspDto] = Json.reads[CAFirstSalaryUaspDto]
  implicit val writes: OWrites[CAFirstSalaryUaspDto] = Json.writes[CAFirstSalaryUaspDto]

}