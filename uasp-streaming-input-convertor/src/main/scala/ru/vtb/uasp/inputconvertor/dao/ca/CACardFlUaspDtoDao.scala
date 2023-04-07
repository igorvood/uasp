package ru.vtb.uasp.inputconvertor.dao.ca

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.{getMapEntry, mapCollect}
import ru.vtb.uasp.inputconvertor.dao.dto.CACardFlUaspDto

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object CACardFlUaspDtoDao {

  def fromJValue(inMessage: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] = {

    val value1 = inMessage.validate[CACardFlUaspDto]
      .map(dj => {
        Json.toJson(
          UaspDto(
            id = dj.hash_card_number,
            uuid = FastUUID.toString(UUID.randomUUID),
            process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
            dataInt = Map.empty,
            dataLong = Map.empty,
            dataFloat = Map.empty,
            dataDouble = Map.empty,
            dataDecimal = mapCollect(getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.ca-cardfl.credit_limit_amt")(0), dj.credit_limit_amt.orNull)),
            dataString = mapCollect(getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.mdmid")(0), dj.mdm_id),
              getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.hash_card_number")(0), dj.hash_card_number),
              getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.hash_sha256md5_card_number")(0), dj.hash_sha256md5_card_number),
              getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.mask_card_number")(0), dj.mask_card_number),
              getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.customer_id_and_masked_card_number")(0), dj.customer_id_and_masked_card_number),
              getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.customer_id")(0), dj.customer_id),
              getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.source_system_cd")(0), dj.source_system_cd),
              getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.pos_flg")(0), dj.pos_flg),
              getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.account_num")(0), dj.account_num),
              getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.is_virtual_card_flg")(0), dj.is_virtual_card_flg.orNull),
              getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.card_expiration_dt")(0), dj.card_expiration_dt.orNull),
              getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.payment_system_desc")(0), dj.payment_system_desc.orNull),
              getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.card_type_cd")(0), dj.card_type_cd.orNull),
              getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.salary_serv_pack_flg")(0), dj.salary_serv_pack_flg.orNull),
              getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.salary_project_flg")(0), dj.salary_project_flg.orNull),
              getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.salary_account_scheme_flg")(0), dj.salary_account_scheme_flg.orNull),
              getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.salary_card_type_flg")(0), dj.salary_card_type_flg.orNull),
              getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.contract_card_type_cd")(0), dj.contract_card_type_cd.orNull),
              getMapEntry[String](dtoMap("app.uaspdto.fields.ca-cardfl.loan_insurance_flg")(0), dj.loan_insurance_flg.orNull)),
            dataBoolean = mapCollect(getMapEntry[Boolean](dtoMap("app.uaspdto.fields.ca-cardfl.is_deleted")(0), dj.is_deleted_bool))
          )
        )
      })
    List(value1)
  }

}
