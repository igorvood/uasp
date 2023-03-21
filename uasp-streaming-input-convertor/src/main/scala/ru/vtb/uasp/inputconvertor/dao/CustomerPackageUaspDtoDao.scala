package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.getMapEntry

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object CustomerPackageUaspDtoDao {
  def fromJValue(inMessage: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] = {

    val value = for {
      mdmId <- (inMessage \ "mdm_id").validate[String]
      packageNm <- (inMessage \ "package_nm").validateOpt[String]
      pensionFlg <- (inMessage \ "pension_flg").validateOpt[String]
      multibonus_flg <- (inMessage \ "multibonus_flg").validateOpt[String]
      is_deleted <- (inMessage \ "is_deleted").validate[String]
      is_deleted_bool = is_deleted match {
        case "1" => true
        case _ => false
      }
    } yield UaspDto(
      id = mdmId,
      uuid = FastUUID.toString(UUID.randomUUID),
      process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,
      dataInt = Map.empty,
      dataLong = Map.empty,
      dataFloat = Map.empty,
      dataDouble = Map.empty,
      dataDecimal = Map.empty,
      dataString = Map(getMapEntry[String](dtoMap("app.uaspdto.fields.mdm_id")(0), mdmId),
        getMapEntry[String](dtoMap("app.uaspdto.fields.package_nm")(0), packageNm.orNull),
        getMapEntry[String](dtoMap("app.uaspdto.fields.multibonus_flg")(0), multibonus_flg.orNull),
        getMapEntry[String](dtoMap("app.uaspdto.fields.is_deleted")(0), is_deleted),
        getMapEntry[String](dtoMap("app.uaspdto.fields.pension_flg")(0), pensionFlg.orNull)
      ),
      dataBoolean = Map(getMapEntry(dtoMap("app.uaspdto.fields.is_deleted")(0), is_deleted_bool))

    )
    List(value)


  }
}
