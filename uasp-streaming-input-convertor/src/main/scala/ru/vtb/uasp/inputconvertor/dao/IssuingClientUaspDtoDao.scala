package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.getMapEntry

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object IssuingClientUaspDtoDao {
  def fromJValue(inMessage: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] = {


    val value = for {
      clientId <- (inMessage \ "id").validate[String]
    } yield UaspDto(
      id = clientId,
      uuid = FastUUID.toString(UUID.randomUUID),
      process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
      dataInt = Map.empty,
      dataLong = Map.empty,
      dataFloat = Map.empty,
      dataDouble = Map.empty,
      dataDecimal = Map.empty,
      dataString = Map(getMapEntry[String](dtoMap("app.uaspdto.fields.local_id")(0), clientId)),
      dataBoolean = Map.empty
    )
    List(value)


  }

}
