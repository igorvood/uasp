package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import org.json4s._
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.getMap

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object CustomerPackageUaspDtoDao {
  def fromJValue(inMessage: JValue, dtoMap: Map[String, Array[String]]): UaspDto = {
    implicit val formats: Formats = DefaultFormats.disallowNull

    lazy val mdmId: String = (inMessage \ "mdm_id").extract[String]
    lazy val packageNm: String = (inMessage \ "package_nm").extract[String]
    lazy val pensionFlg: String = (inMessage \ "pension_flg").extract[String]
    lazy val multibonus_flg: String = (inMessage \ "multibonus_flg").extract[String]
    lazy val is_deleted: String = (inMessage \ "is_deleted").extract[String]

    val dataInt = Map[String, Int]()
    val dataLong = Map[String, Long]()
    val dataFloat = Map[String, Float]()
    val dataDouble = Map[String, Double]()
    val dataDecimal = Map[String, BigDecimal]()
    val dataString = Map[String, String]() ++
      getMap[String](dtoMap("app.uaspdto.fields.mdm_id")(0), mdmId) ++
      getMap[String](dtoMap("app.uaspdto.fields.package_nm")(0), packageNm) ++
      getMap[String](dtoMap("app.uaspdto.fields.multibonus_flg")(0), multibonus_flg) ++
      getMap[String](dtoMap("app.uaspdto.fields.is_deleted")(0), is_deleted) ++
      getMap[String](dtoMap("app.uaspdto.fields.pension_flg")(0), pensionFlg)
    val dataBoolean = Map[String, Boolean]()

    UaspDto(
      id = mdmId,
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
