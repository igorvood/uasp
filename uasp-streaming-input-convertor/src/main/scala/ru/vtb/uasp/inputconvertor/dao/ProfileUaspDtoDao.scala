package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import org.json4s._
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.{dtStringToLong, getMap}
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel
import ru.vtb.uasp.inputconvertor.utils.hash.HashUtils

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object ProfileUaspDtoDao {
  def fromJValue(inMessage: JValue, propsModel: InputPropsModel, dtoMap: Map[String, Array[String]]): UaspDto = {
    implicit val formats: Formats = DefaultFormats.disallowNull

    lazy val systemSource = "Profile"
    lazy val accountType = "MS"
    ///таймзона в которой приходит дата
    lazy val timeZone = "Europe/Moscow"


    lazy val internalId: String = (inMessage \ "cid").extract[String]
    lazy val drcr = (inMessage \ "drcr").extract[String]
    lazy val date = (inMessage \ "cdt").extract[String]
    lazy val time = (inMessage \ "tim").extract[String]
    lazy val tcmt = (inMessage \ "tcmt").extract[String]
    lazy val tso = (inMessage \ "tso").extract[String]
    lazy val bcrcd = (inMessage \ "bcrcd").extract[String]
    lazy val ztsoatmc = (inMessage \ "ztsoatmc").extractOrElse[String]("")

    lazy val eventTime = dtStringToLong(date + "T" + time, "yyyy-MM-dd'T'HHmmss", timeZone)
    lazy val transactionAmount = BigDecimal((inMessage \ "prin").extract[String])
    lazy val bseamt = BigDecimal((inMessage \ "bseamt").extract[String])
    lazy val currencyCode = (inMessage \ "crcd").extract[String]
    lazy val dataKBO = (inMessage \ "ztsotrntype").extract[String]
    lazy val spr = (inMessage \ "spr").extract[String]
    lazy val endbal = BigDecimal((inMessage \ "endbal").extract[String])
    lazy val tcmt_account_num = if (tcmt.contains("Перенос начисленных процентов согласно условиям договора. Вклад") &&
      dataKBO.equals("731720-00")) "\\d{20}".r.findFirstMatchIn(tcmt).map(_.toString()).getOrElse("********************")
    else "********************"
    lazy val data_transactionDate = date + " " + time
    lazy val hash_card_number = HashUtils.getHashSHA256PrependingSalt(ztsoatmc, propsModel.SHA256salt)
    val dataInt = Map[String, Int]()
    val dataLong = Map[String, Long]() ++
      getMap[Long](dtoMap("app.uaspdto.fields.profile.cdt_tim")(0), eventTime)
    val dataFloat = Map[String, Float]()
    val dataDouble = Map[String, Double]()
    val dataDecimal = Map[String, BigDecimal]() ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.profile.prin")(0), transactionAmount) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.profile.endbal")(0), endbal) ++
      getMap[BigDecimal](dtoMap("app.uaspdto.fields.profile.bseamt")(0), bseamt)
    val dataString = Map[String, String]() ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.source_system")(0), systemSource) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.ztsotrntype")(0), dataKBO) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.cid")(0), internalId) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.drcr")(0), drcr) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.tcmt")(0), tcmt) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.ztsoatmc")(0), ztsoatmc) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.crcd")(0), currencyCode) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.spr")(0), spr) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.tcmt_account_num")(0), tcmt_account_num) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.account_type")(0), accountType) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.ztsoatmc.hash")(0), hash_card_number) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.data_transactionDate")(0), data_transactionDate) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.tso")(0), tso) ++
      getMap[String](dtoMap("app.uaspdto.fields.profile.bcrcd")(0), bcrcd)

    val dataBoolean = Map[String, Boolean]()

    UaspDto(
      id = internalId,
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
