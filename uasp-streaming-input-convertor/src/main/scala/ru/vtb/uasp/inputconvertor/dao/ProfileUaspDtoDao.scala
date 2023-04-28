package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.{getMapEntry, mapCollect}
import ru.vtb.uasp.inputconvertor.dao.dto.ProfileUaspDto
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object ProfileUaspDtoDao {

  lazy val systemSource = "Profile"
  lazy val accountType = "MS"
  ///таймзона в которой приходит дата
  lazy val timeZone = "Europe/Moscow"

  def fromJValue(inMessage: JsValue, propsModel: InputPropsModel, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] = {

    val value = inMessage.validate[ProfileUaspDto]

    println(1)

    val value2 = value.map(dj => {
      UaspDto(
        id = dj.cid,
        uuid = FastUUID.toString(UUID.randomUUID),
        process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
        dataInt = Map.empty,
        dataLong = Map(getMapEntry[Long](dtoMap("app.uaspdto.fields.profile.cdt_tim")(0), dj.eventTime)),
        dataFloat = Map.empty,
        dataDouble = Map.empty,
        dataDecimal = mapCollect(getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.profile.prin")(0), dj.prin.orNull),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.profile.endbal")(0), dj.endbal.orNull),
          getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.profile.bseamt")(0), dj.bseamt.orNull)
        ),
        dataString = mapCollect(getMapEntry[String](dtoMap("app.uaspdto.fields.profile.source_system")(0), systemSource),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.ztsotrntype")(0), dj.ztsotrntype.orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.cid")(0), dj.cid),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.drcr")(0), dj.drcr.orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.tcmt")(0), dj.tcmt.orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.ztsoatmc")(0), dj.ztsoatmc.getOrElse("")),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.crcd")(0), dj.crcd.orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.spr")(0), dj.spr),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.tcmt_account_num")(0), dj.tcmt_account_num.orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.account_type")(0), accountType),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.ztsoatmc.hash")(0), dj.SHA_CARD_NUMBER_DKO.getOrElse("")),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.data_transactionDate")(0), dj.data_transactionDate),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.tso")(0), dj.tso.orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.profile.bcrcd")(0), dj.bcrcd.orNull)
        ),
        dataBoolean = Map.empty
      )
    })
    val value1 = value2.map(d => Json.toJson(d))

    List(value1)

  }

}
