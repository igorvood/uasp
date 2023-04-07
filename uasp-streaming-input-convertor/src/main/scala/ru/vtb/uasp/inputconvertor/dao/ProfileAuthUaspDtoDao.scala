package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.getMapEntry
import ru.vtb.uasp.inputconvertor.dao.dto.ProfileAuthUaspDto
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object ProfileAuthUaspDtoDao {

  lazy val systemSource = "Profile-auth"
  ///таймзона в которой приходит дата
  lazy val timeZone = "GMT+0000" //"Europe/Moscow"


  def fromJValue(inMessage: JsValue, propsModel: InputPropsModel, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] = {
    val value1 = inMessage.validate[ProfileAuthUaspDto].map(dj => {
      UaspDto(
        id = dj.msgid,
        uuid = FastUUID.toString(UUID.randomUUID),
        process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
        dataInt = Map(getMapEntry[Int](dtoMap("app.uaspdto.fields.profile.auth.transaction_currency_cd")(0), dj.transaction_currency_cd_decimal),
          getMapEntry[Int](dtoMap("app.uaspdto.fields.profile.auth.typ")(0), dj.typ_decimal)
        ),
        dataLong = Map(getMapEntry[Long](dtoMap("app.uaspdto.fields.profile.auth.local_transaction_dttm")(0), dj.eventTime),
          getMapEntry[Long](dtoMap("app.uaspdto.fields.profile.auth.transmission_dttm")(0), dj.transmission_dttm_formatted)
        ),
        dataFloat = Map.empty,
        dataDouble = Map.empty,
        dataDecimal = Map(getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.profile.auth.transaction_amt")(0), dj.transaction_amt_big_decimal)),
        dataString = Map(getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.source_system")(0), systemSource),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.msgid")(0), dj.msgid),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.transaction_cd")(0), dj.transaction_cd),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.terminal_owner")(0), dj.terminal_owner),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.message_type")(0), dj.message_type),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.card_number")(0), dj.card_number),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.response_code")(0), dj.response_code),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.currency_alpha_code")(0), dj.currency_alpha_code),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.card_numberHashSHA256")(0), dj.SHA_CARD_NUMBER_DKO.getOrElse("")),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.card_numberHashSHA256MD5")(0), dj.sha_card_number.getOrElse("")),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.terminal_class")(0), dj.terminal_class),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.date_time_string")(0), dj.transmission_dttm),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.card_acceptor_terminal_ident")(0), dj.card_acceptor_terminal_ident),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.merchant_category_cd")(0), dj.merchant_category_cd),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.replacement_amt")(0), dj.replacement_amt)
        ),
        dataBoolean = Map.empty
      )
    }).map(d => Json.toJson(d))
    List(value1)


  }


}
