package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.{getMapEntry, mapCollect}
import ru.vtb.uasp.inputconvertor.dao.dto.LoyaltyUaspDto
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel
import ru.vtb.uasp.inputconvertor.utils.hash.HashUtils

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object LoyaltyUaspDtoDao {
  lazy val systemSource = "loyalty"

  def fromJValue(inMessage: JsValue, propsModel: InputPropsModel, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] = {

    val value = inMessage.validate[LoyaltyUaspDto]

    val value1 = value.map(d => {

      val option = d.loyaltyPrograms.flatMap(_.options).last

      UaspDto(
        id = d.mdmId,
        uuid = FastUUID.toString(UUID.randomUUID),
        process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
        dataInt = Map.empty,
        dataLong = Map(getMapEntry[Long](dtoMap("app.uaspdto.fields.loyalty.eventDttm")(0), d.actualTime)),
        dataFloat = Map.empty,
        dataDouble = Map.empty,
        dataDecimal = Map.empty,
        dataString = mapCollect(
          getMapEntry[String](dtoMap("app.uaspdto.fields.loyalty.mdmId")(0), d.mdmId),
          getMapEntry[String](dtoMap("app.uaspdto.fields.loyalty.source_system")(0), systemSource),
          getMapEntry[String](dtoMap("app.uaspdto.fields.loyalty.loyaltyCode")(0), option.code),
          getMapEntry[String](dtoMap("app.uaspdto.fields.loyalty.empty.string.hash")(0), HashUtils.getHashSHA256PrependingSalt("", propsModel.sha256salt)),
          getMapEntry[String](dtoMap("app.uaspdto.fields.loyalty.eventType")(0), d.eventType.getOrElse(""))


        ),
        dataBoolean = Map.empty
      )
    }
    ).map(d => Json.toJson(d))
    List(value1)

  }

}

