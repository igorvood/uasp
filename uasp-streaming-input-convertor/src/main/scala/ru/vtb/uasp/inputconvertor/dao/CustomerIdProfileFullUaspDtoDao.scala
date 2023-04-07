package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.getMapEntry
import ru.vtb.uasp.inputconvertor.dao.dto.CustomerIdProfileFullUaspDto

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object CustomerIdProfileFullUaspDtoDao {
  def fromJValue(inMessage: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] = {
    val value1 = inMessage.validate[CustomerIdProfileFullUaspDto].map(dj => {
      Json.toJson(
        UaspDto(
          id = dj.contract_id,
          uuid = FastUUID.toString(UUID.randomUUID),
          process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
          dataInt = Map.empty,
          dataLong = Map.empty,
          dataFloat = Map.empty,
          dataDouble = Map.empty,
          dataDecimal = Map.empty,
          dataString = Map(getMapEntry[String](dtoMap("app.uaspdto.fields.contract_id")(0), dj.contract_id),
            getMapEntry[String](dtoMap("app.uaspdto.fields.customer_id")(0), dj.customer_id),
            getMapEntry[String](dtoMap("app.uaspdto.fields.contract_num")(0), dj.contract_num.orNull)
          ),
          dataBoolean = Map.empty
        )
      )
    })

    List(value1)

  }
}
