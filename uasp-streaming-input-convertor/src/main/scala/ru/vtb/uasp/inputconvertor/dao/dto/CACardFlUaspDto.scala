package ru.vtb.uasp.inputconvertor.dao.dto

import play.api.libs.json.{Json, OWrites, Reads}

case class CACardFlUaspDto(
                            mdm_id: String,
                            hash_card_number: String,
                            hash_sha256md5_card_number: String,
                            mask_card_number: String,
                            customer_id_and_masked_card_number: String,
                            customer_id: String,
                            source_system_cd: String,
                            pos_flg: String,
                            account_num: String,
                            is_virtual_card_flg: Option[String],
                            card_expiration_dt: Option[String],
                            payment_system_desc: Option[String],
                            card_type_cd: Option[String],
                            salary_serv_pack_flg: Option[String],
                            salary_project_flg: Option[String],
                            salary_account_scheme_flg: Option[String],
                            salary_card_type_flg: Option[String],
                            contract_card_type_cd: Option[String],
                            credit_limit_amt: Option[BigDecimal],
                            loan_insurance_flg: Option[String],
                            is_deleted: String) {
  val is_deleted_bool: Boolean = is_deleted match {
    case "1" => true
    case "0" => false
    case er => throw new IllegalArgumentException(s"is_deleted must be 0 or 1, current value is $er")

  }
}

object CACardFlUaspDto {
  implicit val reads: Reads[CACardFlUaspDto] = Json.reads[CACardFlUaspDto]
  implicit val writes: OWrites[CACardFlUaspDto] = Json.writes[CACardFlUaspDto]
}