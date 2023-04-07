package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsError, JsResult, JsSuccess, JsValue, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.getMapEntry
import ru.vtb.uasp.inputconvertor.dao.dto.CustomerPackageUaspDto

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object CustomerPackageUaspDtoDao {
  private val TRUE = JsSuccess(true)
  private val FALSE = JsSuccess(false)

  def fromJValue(inMessage: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] = {

    val value1 = inMessage.validate[CustomerPackageUaspDto].map(dj => {
      Json.toJson(
      UaspDto(
        id = dj.mdm_id,
        uuid = FastUUID.toString(UUID.randomUUID),
        process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
        dataInt = Map.empty,
        dataLong = Map.empty,
        dataFloat = Map.empty,
        dataDouble = Map.empty,
        dataDecimal = Map.empty,
        dataString = Map(getMapEntry[String](dtoMap("app.uaspdto.fields.mdm_id")(0), dj.mdm_id),
          getMapEntry[String](dtoMap("app.uaspdto.fields.package_nm")(0), dj.package_nm.orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.multibonus_flg")(0), dj.multibonus_flg.orNull),
          getMapEntry[String](dtoMap("app.uaspdto.fields.is_deleted")(0), dj.is_deleted),
          getMapEntry[String](dtoMap("app.uaspdto.fields.pension_flg")(0), dj.pension_flg.orNull)
        ),
        dataBoolean = Map(getMapEntry(dtoMap("app.uaspdto.fields.is_deleted")(0), dj.is_deleted_bool))
      )
      )
    })

    List(value1)

  }
}
