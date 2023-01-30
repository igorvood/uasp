package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import org.json4s._
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.{dtStringToLong, findAndMaskNumber, getMap}
import ru.vtb.uasp.inputconvertor.utils.CurrencyConverter.returnAlphaCode
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel
import ru.vtb.uasp.inputconvertor.utils.hash.HashUtils

import java.time.{LocalDate, LocalDateTime, ZoneId}
import java.util.{Calendar, UUID}

object ProfileAuthUaspDtoDao {
  def fromJValue(inMessage: JValue, propsModel: InputPropsModel, dtoMap: Map[String, Array[String]]): UaspDto = {
    implicit val formats: Formats = DefaultFormats.disallowNull
    lazy val systemSource = "Profile-auth"
    ///таймзона в которой приходит дата
    lazy val timeZone = "GMT+0000" //"Europe/Moscow"


    lazy val msgid: String = (inMessage \ "msgid").extract[String]
    lazy val unMaskCardNumber = (inMessage \ "card_number").extract[String]
    lazy val card_number = findAndMaskNumber(unMaskCardNumber)
    lazy val hashCardNumber = HashUtils.getHashSHA256PrependingSalt(unMaskCardNumber,propsModel.SHA256salt)
    lazy val hashCardNumberSha256Md5 = HashUtils.getHashSHA256AppendingSalt(HashUtils.getMD5Hash(unMaskCardNumber),propsModel.SHA256salt)
    lazy val transaction_amt = BigDecimal((inMessage \ "transaction_amt").extract[String])
    lazy val transaction_currency_cd = (inMessage \ "transaction_currency_cd").extract[String].toInt
    lazy val currency_alpha_code = returnAlphaCode(transaction_currency_cd.toString)
    lazy val typ = (inMessage \ "typ").extract[String].toInt
    lazy val date = (inMessage \ "local_transaction_dt").extract[String]
    lazy val time = (inMessage \ "local_transaction_ts").extract[String]
    lazy val merchant_category_cd  = (inMessage \ "merchant_category_cd").extract[String]
    lazy val transmission_dttm = (inMessage \ "transmission_dttm").extract[String]
    lazy val replacement_amt = (inMessage \ "replacement_amt").extract[String]
    //FIXME дата приходит без года?
    lazy val currentYear = Calendar.getInstance.get(Calendar.YEAR)
    //Вычиляем корректность даты, если входящая дата + currentYear больше чем текущая дата то берем предыдущий год для даты
    lazy val calculatedYear = if (LocalDate.parse(currentYear.toString + "-" + date.substring(0, 2) + "-" + date.substring(2, 4)).
      isAfter(LocalDate.now())) {
      currentYear - 1
    } else currentYear
    lazy val eventTime = dtStringToLong(calculatedYear.toString + date + "T" + time, "yyyyMMdd'T'HHmmss", timeZone)
    lazy val transmission_dttm_formatted = dtStringToLong(calculatedYear.toString + transmission_dttm, "yyyyMMddHHmmss", timeZone)
    lazy val response_code = (inMessage \ "response_code").extract[String]
    lazy val terminal_class = (inMessage \ "terminal_class").extract[String]
    lazy val transaction_cd = (inMessage \ "transaction_cd").extract[String]
    lazy val terminal_owner = (inMessage \ "terminal_owner").extract[String]
    lazy val message_type = (inMessage \ "message_type").extract[String]
    lazy val card_acceptor_terminal_ident = (inMessage \ "card_acceptor_terminal_ident").extract[String]

    val dataInt = Map[String, Int]() ++
      getMap[Int](dtoMap("app.uaspdto.fields.profile.auth.transaction_currency_cd")(0), transaction_currency_cd) ++
      getMap[Int](dtoMap("app.uaspdto.fields.profile.auth.typ")(0), typ)
    val dataLong = Map[String, Long]() ++
      getMap[Long](dtoMap("app.uaspdto.fields.profile.auth.local_transaction_dttm")(0), eventTime) ++
      getMap[Long](dtoMap("app.uaspdto.fields.profile.auth.transmission_dttm")(0), transmission_dttm_formatted)
    val dataFloat = Map[String, Float]()
    val dataDouble = Map[String, Double]()
    val dataDecimal = Map[String, BigDecimal]() ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.profile.auth.transaction_amt")(0), transaction_amt)
    val dataString = Map[String, String]() ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.auth.source_system")(0), systemSource) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.auth.msgid")(0), msgid) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.auth.transaction_cd")(0), transaction_cd) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.auth.terminal_owner")(0), terminal_owner) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.auth.message_type")(0), message_type) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.auth.card_number")(0), card_number) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.auth.response_code")(0), response_code) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.auth.currency_alpha_code")(0), currency_alpha_code) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.auth.card_numberHashSHA256")(0), hashCardNumber) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.auth.card_numberHashSHA256MD5")(0), hashCardNumberSha256Md5)++
      getMap[String](dtoMap("app.uaspdto.fields.profile.auth.terminal_class")(0), terminal_class) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.auth.date_time_string")(0), transmission_dttm) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.auth.card_acceptor_terminal_ident")(0), card_acceptor_terminal_ident) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.auth.merchant_category_cd")(0), merchant_category_cd ) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.auth.replacement_amt")(0), replacement_amt)

    val dataBoolean = Map[String, Boolean]()

    UaspDto(
      id = msgid,
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
