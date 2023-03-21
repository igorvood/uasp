package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.{dtStringToLong, findAndMaskNumber, getMapEntry}
import ru.vtb.uasp.inputconvertor.utils.CurrencyConverter.returnAlphaCode
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

import java.time.{LocalDate, LocalDateTime, ZoneId}
import java.util.{Calendar, UUID}

object ProfileAuthUaspDtoDao {

  lazy val systemSource = "Profile-auth"
  ///таймзона в которой приходит дата
  lazy val timeZone = "GMT+0000" //"Europe/Moscow"


  def fromJValue(inMessage: JsValue, propsModel: InputPropsModel, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] = {


    val value = for {
      msgid <- (inMessage \ "msgid").validate[String]
      unMaskCardNumber <- (inMessage \ "card_number").validate[String]
      hashCardNumber <- (inMessage \ "SHA_CARD_NUMBER_DKO").validateOpt[String].map(q => q.getOrElse(""))
      hashCardNumberSha256Md5 <- (inMessage \ "sha_card_number").validateOpt[String].map(q => q.getOrElse(""))
      card_number = findAndMaskNumber(unMaskCardNumber)
      transaction_amt <- (inMessage \ "transaction_amt").validate[String].map(s => BigDecimal(s))
      transaction_currency_cd <- (inMessage \ "transaction_currency_cd").validate[String].map(s => s.toInt)
      currency_alpha_code = returnAlphaCode(transaction_currency_cd.toString)
      typ <- (inMessage \ "typ").validate[String].map(s => s.toInt)
      date <- (inMessage \ "local_transaction_dt").validate[String]
      time <- (inMessage \ "local_transaction_ts").validate[String]
      merchant_category_cd <- (inMessage \ "merchant_category_cd").validate[String]
      transmission_dttm <- (inMessage \ "transmission_dttm").validate[String]
      replacement_amt <- (inMessage \ "replacement_amt").validate[String]
      //FIXME дата приходит без года?
      currentYear = Calendar.getInstance.get(Calendar.YEAR)
      //Вычиляем корректность даты, если входящая дата + currentYear больше чем текущая дата то берем предыдущий год для даты
      calculatedYear = if (LocalDate.parse(currentYear.toString + "-" + transmission_dttm.substring(0, 2) + "-" + transmission_dttm.substring(2, 4)).
        isAfter(LocalDate.now())) {
        currentYear - 1
      } else currentYear
      eventTime = dtStringToLong(calculatedYear.toString + date + "T" + time, "yyyyMMdd'T'HHmmss", timeZone)
      transmission_dttm_formatted = dtStringToLong(calculatedYear.toString + transmission_dttm, "yyyyMMddHHmmss", timeZone)
      response_code <- (inMessage \ "response_code").validate[String]
      terminal_class <- (inMessage \ "terminal_class").validate[String]
      transaction_cd <- (inMessage \ "transaction_cd").validate[String]
      terminal_owner <- (inMessage \ "terminal_owner").validate[String]
      message_type <- (inMessage \ "message_type").validate[String]
      card_acceptor_terminal_ident <- (inMessage \ "card_acceptor_terminal_ident").validate[String]
    } yield UaspDto(
      id = msgid,
      uuid = FastUUID.toString(UUID.randomUUID),
      process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
      dataInt = Map(getMapEntry[Int](dtoMap("app.uaspdto.fields.profile.auth.transaction_currency_cd")(0), transaction_currency_cd),
        getMapEntry[Int](dtoMap("app.uaspdto.fields.profile.auth.typ")(0), typ)
      ),
      dataLong = Map(getMapEntry[Long](dtoMap("app.uaspdto.fields.profile.auth.local_transaction_dttm")(0), eventTime),
        getMapEntry[Long](dtoMap("app.uaspdto.fields.profile.auth.transmission_dttm")(0), transmission_dttm_formatted)
      ),
      dataFloat = Map.empty,
      dataDouble = Map.empty,
      dataDecimal = Map(getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.profile.auth.transaction_amt")(0), transaction_amt)),
      dataString = Map(getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.source_system")(0), systemSource),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.msgid")(0), msgid),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.transaction_cd")(0), transaction_cd),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.terminal_owner")(0), terminal_owner),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.message_type")(0), message_type),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.card_number")(0), card_number),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.response_code")(0), response_code),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.currency_alpha_code")(0), currency_alpha_code),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.card_numberHashSHA256")(0), hashCardNumber),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.card_numberHashSHA256MD5")(0), hashCardNumberSha256Md5),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.terminal_class")(0), terminal_class),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.date_time_string")(0), transmission_dttm),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.card_acceptor_terminal_ident")(0), card_acceptor_terminal_ident),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.merchant_category_cd")(0), merchant_category_cd),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.auth.replacement_amt")(0), replacement_amt)
      ),
      dataBoolean = Map.empty
    )
    List(value)


  }


}
