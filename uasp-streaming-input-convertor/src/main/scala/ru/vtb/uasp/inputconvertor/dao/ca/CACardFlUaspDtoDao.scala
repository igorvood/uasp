package ru.vtb.uasp.inputconvertor.dao.ca

import com.eatthepath.uuid.FastUUID
import org.json4s.{DefaultFormats, Formats, JValue}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.getMap

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object CACardFlUaspDtoDao {
  def fromJValue(inMessage: JValue, dtoMap: Map[String, Array[String]]): UaspDto = {
    implicit val formats: Formats = DefaultFormats.disallowNull

    lazy val mdmid: String = (inMessage \ "mdm_id").extract[String]
    lazy val hash_card_number: String = (inMessage \ "hash_card_number").extract[String]
    lazy val hash_sha256md5_card_number: String = (inMessage \ "hash_sha256md5_card_number").extract[String]
    lazy val mask_card_number: String = (inMessage \ "mask_card_number").extract[String]
    lazy val customer_id_and_masked_card_number: String = (inMessage \ "customer_id_and_masked_card_number").extract[String]
    lazy val customer_id: String = (inMessage \ "customer_id").extract[String]
    lazy val source_system_cd: String = (inMessage \ "source_system_cd").extract[String]
    lazy val pos_flg = (inMessage \ "pos_flg").extract[String]
    lazy val account_num: String = (inMessage \ "account_num").extract[String]
    lazy val is_virtual_card_flg: String = (inMessage \ "is_virtual_card_flg").extract[String]
    lazy val card_expiration_dt: String = (inMessage \ "card_expiration_dt").extract[String]
    lazy val payment_system_desc: String = (inMessage \ "payment_system_desc").extract[String]
    lazy val card_type_cd: String = (inMessage \ "card_type_cd").extract[String]
    lazy val salary_serv_pack_flg: String = (inMessage \ "salary_serv_pack_flg").extract[String]
    lazy val salary_project_flg: String = (inMessage \ "salary_project_flg").extract[String]
    lazy val salary_account_scheme_flg: String = (inMessage \ "salary_account_scheme_flg").extract[String]
    lazy val salary_card_type_flg: String = (inMessage \ "salary_card_type_flg").extract[String]
    lazy val contract_card_type_cd: String = (inMessage \ "contract_card_type_cd").extract[String]
    lazy val credit_limit_amt: BigDecimal = (inMessage \ "credit_limit_amt").extract[BigDecimal]
    lazy val loan_insurance_flg: String = (inMessage \ "loan_insurance_flg").extract[String]

    val dataInt = Map[String, Int]()
    val dataLong = Map[String, Long]()
    val dataFloat = Map[String, Float]()
    val dataDouble = Map[String, Double]()
    val dataDecimal = Map[String, BigDecimal]() ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.ca-cardfl.credit_limit_amt")(0), credit_limit_amt)

    val dataString = Map[String, String]() ++
      getMap[String](dtoMap("app.uaspdto.fields.ca-cardfl.mdmid")(0), mdmid) ++
      getMap[String](dtoMap("app.uaspdto.fields.ca-cardfl.hash_card_number")(0), hash_card_number) ++
      getMap[String](dtoMap("app.uaspdto.fields.ca-cardfl.hash_sha256md5_card_number")(0), hash_sha256md5_card_number) ++
      getMap[String](dtoMap("app.uaspdto.fields.ca-cardfl.mask_card_number")(0), mask_card_number) ++
      getMap[String](dtoMap("app.uaspdto.fields.ca-cardfl.customer_id_and_masked_card_number")(0), customer_id_and_masked_card_number) ++
      getMap[String](dtoMap("app.uaspdto.fields.ca-cardfl.customer_id")(0), customer_id) ++
      getMap[String](dtoMap("app.uaspdto.fields.ca-cardfl.source_system_cd")(0), source_system_cd) ++
      getMap[String](dtoMap("app.uaspdto.fields.ca-cardfl.pos_flg")(0), pos_flg) ++
      getMap[String](dtoMap("app.uaspdto.fields.ca-cardfl.account_num")(0), account_num) ++
      getMap[String](dtoMap("app.uaspdto.fields.ca-cardfl.is_virtual_card_flg")(0), is_virtual_card_flg) ++
      getMap[String](dtoMap("app.uaspdto.fields.ca-cardfl.card_expiration_dt")(0), card_expiration_dt) ++
      getMap[String](dtoMap("app.uaspdto.fields.ca-cardfl.payment_system_desc")(0), payment_system_desc) ++
      getMap[String](dtoMap("app.uaspdto.fields.ca-cardfl.card_type_cd")(0), card_type_cd) ++
      getMap[String](dtoMap("app.uaspdto.fields.ca-cardfl.salary_serv_pack_flg")(0), salary_serv_pack_flg) ++
      getMap[String](dtoMap("app.uaspdto.fields.ca-cardfl.salary_project_flg")(0), salary_project_flg) ++
      getMap[String](dtoMap("app.uaspdto.fields.ca-cardfl.salary_account_scheme_flg")(0), salary_account_scheme_flg) ++
      getMap[String](dtoMap("app.uaspdto.fields.ca-cardfl.salary_card_type_flg")(0), salary_card_type_flg) ++
      getMap[String](dtoMap("app.uaspdto.fields.ca-cardfl.contract_card_type_cd")(0), contract_card_type_cd) ++
      getMap[String](dtoMap("app.uaspdto.fields.ca-cardfl.loan_insurance_flg")(0), loan_insurance_flg)

    val dataBoolean = Map[String, Boolean]()

    UaspDto(
      id = hash_card_number,
      uuid = FastUUID.toString(UUID.randomUUID),
      process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
      dataInt = dataInt,
      dataLong = dataLong,
      dataFloat = dataFloat,
      dataDouble = dataDouble,
      dataDecimal = dataDecimal,
      dataString = dataString,
      dataBoolean = dataBoolean
    )
  }

}
