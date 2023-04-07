package ru.vtb.uasp.inputconvertor.dao.ca

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.getMapEntry
import ru.vtb.uasp.inputconvertor.dao.dto.CADepositFlUaspDto

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object CADepositFlUaspDtoDao {
  def fromJValue(inMessage: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] = {

    val value1 = inMessage.validate[CADepositFlUaspDto]
      .map(dj => {
        Json.toJson(UaspDto(
          id = dj.account_num,
          uuid = FastUUID.toString(UUID.randomUUID),
          process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
          dataInt = Map(getMapEntry[Int](dtoMap("app.uaspdto.fields.depositfl.period")(0), dj.period)),
          dataLong = Map.empty,
          dataFloat = Map.empty,
          dataDouble = Map.empty,
          dataDecimal = Map(getMapEntry[BigDecimal](dtoMap("app.uaspdto.fields.depositfl.product_rate")(0), dj.product_rate)),
          dataString = Map(getMapEntry[String](dtoMap("app.uaspdto.fields.depositfl.account_num")(0), dj.account_num),
            getMapEntry[String](dtoMap("app.uaspdto.fields.depositfl.mdmid")(0), dj.mdm_id),
            getMapEntry[String](dtoMap("app.uaspdto.fields.depositfl.product_nm")(0), dj.product_nm)
          ),
          dataBoolean = Map.empty
        ))
      })

    List(value1)
  }

}
