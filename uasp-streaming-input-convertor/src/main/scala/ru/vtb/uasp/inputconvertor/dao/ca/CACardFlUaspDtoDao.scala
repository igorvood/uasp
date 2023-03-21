package ru.vtb.uasp.inputconvertor.dao.ca

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.{getMapEntry, mapCollect}

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object CACardFlUaspDtoDao {
  def fromJValue(inMessage: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] = {

    val value = for {
      mdmid <- (inMessage \ "mdm_id").validate[String]
      hash_card_number <- (inMessage \ "hash_card_number").validate[String]
      hash_sha256md5_card_number <- (inMessage \ "hash_sha256md5_card_number").validate[String]
      mask_card_number <- (inMessage \ "mask_card_number").validate[String]
      customer_id_and_masked_card_number <- (inMessage \ "customer_id_and_masked_card_number").validate[String]
      customer_id <- (inMessage \ "customer_id").validate[String]
      source_system_cd <- (inMessage \ "source_system_cd").validate[String]
      pos_flg <- (inMessage \ "pos_flg").validate[String]
      account_num <- (inMessage \ "account_num").validate[String]
      is_virtual_card_flg <- (inMessage \ "is_virtual_card_flg").validateOpt[String]
      card_expiration_dt <- (inMessage \ "card_expiration_dt").validateOpt[String]
      payment_system_desc <- (inMessage \ "payment_system_desc").validateOpt[String]
      card_type_cd <- (inMessage \ "card_type_cd").validateOpt[String]
      salary_serv_pack_flg <- (inMessage \ "salary_serv_pack_flg").validateOpt[String]
      salary_project_flg <- (inMessage \ "salary_project_flg").validateOpt[String]
      salary_account_scheme_flg <- (inMessage \ "salary_account_scheme_flg").validateOpt[String]
      salary_card_type_flg <- (inMessage \ "salary_card_type_flg").validateOpt[String]
      contract_card_type_cd <- (inMessage \ "contract_card_type_cd").validateOpt[String]
      credit_limit_amt <- (inMessage \ "credit_limit_amt").validateOpt[BigDecimal]
      loan_insurance_flg <- (inMessage \ "loan_insurance_flg").validateOpt[String]
      is_deleted <- (inMessage \ "is_deleted").validate[String]
        .map {
          case "1" => true
          case _ => false

        }
    } yield UaspDto(
      id = hash_card_number,
      uuid = FastUUID.toString(UUID.randomUUID),
      process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
      dataInt = Map.empty,
      dataLong = Map.empty,
      dataFloat = Map.empty,
      dataDouble = Map.empty,
      dataDecimal = mapCollect(getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.ca-cardfl.credit_limit_amt")(0), credit_limit_amt.orNull)),
      dataString = mapCollect(getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.mdmid")(0), mdmid),
        getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.hash_card_number")(0), hash_card_number),
        getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.hash_sha256md5_card_number")(0), hash_sha256md5_card_number),
        getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.mask_card_number")(0), mask_card_number),
        getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.customer_id_and_masked_card_number")(0), customer_id_and_masked_card_number),
        getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.customer_id")(0), customer_id),
        getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.source_system_cd")(0), source_system_cd),
        getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.pos_flg")(0), pos_flg),
        getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.account_num")(0), account_num),
        getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.is_virtual_card_flg")(0), is_virtual_card_flg.orNull),
        getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.card_expiration_dt")(0), card_expiration_dt.orNull),
        getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.payment_system_desc")(0), payment_system_desc.orNull),
        getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.card_type_cd")(0), card_type_cd.orNull),
        getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.salary_serv_pack_flg")(0), salary_serv_pack_flg.orNull),
        getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.salary_project_flg")(0), salary_project_flg.orNull),
        getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.salary_account_scheme_flg")(0), salary_account_scheme_flg.orNull),
        getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.salary_card_type_flg")(0), salary_card_type_flg.orNull),
        getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.contract_card_type_cd")(0), contract_card_type_cd.orNull),
        getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.loan_insurance_flg")(0), loan_insurance_flg.orNull)),
      dataBoolean = mapCollect(getMapEntry[Boolean](dtoMap("app.uaspdto.fields.ca-cardfl.is_deleted")(0), is_deleted))
    )


    List(value)
  }

}
