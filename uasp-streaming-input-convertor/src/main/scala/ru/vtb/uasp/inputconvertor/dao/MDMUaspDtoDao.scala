package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import ru.vtb.uasp.common.dto.UaspDto
import org.json4s._
import org.json4s.jackson.JsonMethods._
import ru.vtb.uasp.inputconvertor.dao.CommonDao.getMap

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object MDMUaspDtoDao {
  def fromJValue(imMessage: JValue, dtoMap: Map[String, Array[String]], uaspDtoType: String): UaspDto = {
    lazy val sysNumber = uaspDtoType.toUpperCase() match {
      case "MDM" => "OW4"
      case "MDM-PROFILE" => "99995"
    }
    lazy val result = for {
      JArray(contact) <- imMessage
      JObject(contact) <- contact
      JField("partyUId", JString(partyUId)) <- contact
      JField("cifcontactReference", JArray(cifcontactReference)) <- contact
      JObject(cifcontactReference) <- cifcontactReference
      JField("externalId", JString(externalId)) <- cifcontactReference
      JField("systemNumber", JString(systemNumber)) <- cifcontactReference
      if systemNumber == sysNumber
    } yield (partyUId, externalId)


    val dataInt = Map[String, Int]()
    val dataLong = Map[String, Long]()
    val dataFloat = Map[String, Float]()
    val dataDouble = Map[String, Double]()
    val dataDecimal = Map[String, BigDecimal]()
    val dataString = Map[String, String]() ++
      getMap[String](dtoMap("app.uaspdto.fields.local_id")(0), result.head._2) ++
      getMap[String](dtoMap("app.uaspdto.fields.global_id")(0), result.head._1) ++
      getMap[String](dtoMap("app.uaspdto.fields.system_number")(0), sysNumber)
    val dataBoolean = Map[String, Boolean]()

    UaspDto(
      id = dataString(dtoMap("app.uaspdto.fields.local_id")(0)),
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
