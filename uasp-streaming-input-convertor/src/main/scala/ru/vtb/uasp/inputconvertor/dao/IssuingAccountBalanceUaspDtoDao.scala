package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.getMapEntry
import ru.vtb.uasp.inputconvertor.dao.dto.IssuingAccountBalanceUaspDto

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object IssuingAccountBalanceUaspDtoDao {
  def fromJValue(inMessage: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] = {
    val value1 = inMessage.validate[IssuingAccountBalanceUaspDto]
      .map { dj =>
        UaspDto(
          id = dj.id,
          uuid = FastUUID.toString(UUID.randomUUID),
          process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
          dataInt = Map.empty,
          dataLong = Map.empty,
          dataFloat = Map.empty,
          dataDouble = Map.empty,
          dataDecimal = Map.empty,
          dataString = Map(getMapEntry[String](dtoMap("app.uaspdto.fields.local_id")(0), dj.id)),
          dataBoolean = Map.empty
        )
      }.map(d => Json.toJson(d))
    List(value1)

  }


}
