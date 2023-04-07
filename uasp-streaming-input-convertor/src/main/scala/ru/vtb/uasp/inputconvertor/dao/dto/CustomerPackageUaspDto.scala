package ru.vtb.uasp.inputconvertor.dao.dto

import play.api.libs.json.{Json, OWrites, Reads}

case class CustomerPackageUaspDto(
                                   mdm_id: String,
                                   package_nm: Option[String],
                                   pension_flg: Option[String],
                                   multibonus_flg: Option[String],
                                   is_deleted: String,
                                 ) {
  val is_deleted_bool: Boolean =
    is_deleted match {
      case "1" => true
      case "0" => false
      case er => throw new IllegalArgumentException(s"is_deleted must be 0 or 1, current value is $er")
    }

}


object CustomerPackageUaspDto {
  implicit val reads: Reads[CustomerPackageUaspDto] = Json.reads[CustomerPackageUaspDto]
  implicit val writes: OWrites[CustomerPackageUaspDto] = Json.writes[CustomerPackageUaspDto]
}
