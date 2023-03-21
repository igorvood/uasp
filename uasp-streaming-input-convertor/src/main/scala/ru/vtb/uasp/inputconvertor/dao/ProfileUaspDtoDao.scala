package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.{dtStringToLong, getMapEntry, mapCollect}
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object ProfileUaspDtoDao {

  lazy val systemSource = "Profile"
  lazy val accountType = "MS"
  ///таймзона в которой приходит дата
  lazy val timeZone = "Europe/Moscow"


  def fromJValue(inMessage: JsValue, propsModel: InputPropsModel, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] = {


    val value = for {
      internalId <- (inMessage \ "cid").validate[String]
      drcr <- (inMessage \ "drcr").validate[String]
      date <- (inMessage \ "cdt").validate[String]
      time <- (inMessage \ "tim").validate[String]
      tcmt <- (inMessage \ "tcmt").validate[String]
      tso <- (inMessage \ "tso").validate[String]
      bcrcd <- (inMessage \ "bcrcd").validateOpt[String]
      ztsoatmc <- (inMessage \ "ztsoatmc").validateOpt[String].map(w => w.getOrElse(""))
      //
      eventTime = dtStringToLong(date + "T" + time, "yyyy-MM-dd'T'HHmmss", timeZone)
      transactionAmount <- (inMessage \ "prin").validate[BigDecimal]
      bseamt <- (inMessage \ "bseamt").validateOpt[BigDecimal]
      currencyCode <- (inMessage \ "crcd").validate[String]
      dataKBO <- (inMessage \ "ztsotrntype").validate[String]
      spr <- (inMessage \ "spr").validate[String]
      endbal <- (inMessage \ "endbal").validate[BigDecimal]
      tcmt_account_num = if (tcmt.contains("Перенос начисленных процентов согласно условиям договора. Вклад") &&
        dataKBO.equals("731720-00")) "\\d{20}".r.findFirstMatchIn(tcmt).map(_.toString()).getOrElse("********************")
      else "********************"
      data_transactionDate = date + " " + time
      hash_card_number <- (inMessage \ "SHA_CARD_NUMBER_DKO").validateOpt[String].map(w => w.getOrElse(""))


    } yield UaspDto(
      id = internalId,
      uuid = FastUUID.toString(UUID.randomUUID),
      process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
      dataInt = Map.empty,
      dataLong = Map(getMapEntry[Long](dtoMap("app.uaspdto.fields.profile.cdt_tim")(0), eventTime)),
      dataFloat = Map.empty,
      dataDouble = Map.empty,
      dataDecimal = mapCollect(getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.profile.prin")(0), transactionAmount),
        getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.profile.endbal")(0), endbal),
        getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.profile.bseamt")(0), bseamt.orNull)
      ),
      dataString = mapCollect(getMapEntry[String](dtoMap("app.uaspdto.fields.profile.source_system")(0), systemSource),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.ztsotrntype")(0), dataKBO),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.cid")(0), internalId),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.drcr")(0), drcr),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.tcmt")(0), tcmt),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.ztsoatmc")(0), ztsoatmc),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.crcd")(0), currencyCode),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.spr")(0), spr),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.tcmt_account_num")(0), tcmt_account_num),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.account_type")(0), accountType),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.ztsoatmc.hash")(0), hash_card_number),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.data_transactionDate")(0), data_transactionDate),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.tso")(0), tso),
        getMapEntry[String](dtoMap("app.uaspdto.fields.profile.bcrcd")(0), bcrcd.orNull)
      ),
      dataBoolean = Map.empty
    )
    List(value)


  }


}
