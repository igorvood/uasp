package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsError, JsResult, JsSuccess, JsValue, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.getMapEntry
import ru.vtb.uasp.inputconvertor.dao.dto.SysDtoParam
import ru.vtb.uasp.inputconvertor.service.TransformHelper.splitMessage

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object MDMUaspDtoDao {
  def fromJValue(imMessage: JsValue, dtoMap: Map[String, Array[String]], uaspDtoType: String): List[JsResult[JsValue]] = {
    val expectedSysNumber = uaspDtoType.toUpperCase() match {
      case "MDM" => "OW4"
      case "MDM-PROFILE" => "99995"
    }

    val seq = splitMessage[SysDtoParam](imMessage, "contact")
      .map(d1 => {
        d1.map { d =>
          val dtoes = d.cifcontactReference
            .filter(p => p.systemNumber == expectedSysNumber)
            .map(q => {
              UaspDto(id = q.externalId,
                dataInt = Map.empty,
                dataLong = Map.empty,
                dataFloat = Map.empty,
                dataDouble = Map.empty,
                dataDecimal = Map.empty,
                dataString = Map(getMapEntry(dtoMap("app.uaspdto.fields.local_id")(0), q.externalId),
                  getMapEntry(dtoMap("app.uaspdto.fields.global_id")(0), d.partyUId),
                  getMapEntry(dtoMap("app.uaspdto.fields.system_number")(0), q.systemNumber)
                ),
                dataBoolean = Map(getMapEntry(dtoMap("app.uaspdto.fields.is_deleted")(0), q.is_deleted)),
                uuid = FastUUID.toString(UUID.randomUUID),
                process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,

              )
            }).map(d => Json.toJson(d))
          dtoes
        }
      }
      )


    val list = seq
      .foldLeft[List[JsResult[JsValue]]](List()) { (acc, ew) =>
        val value1: List[JsResult[JsValue]] = ew match {
          case JsSuccess(value, path) => value.map(JsSuccess(_))
          case JsError(errors) => List(JsError(errors))
        }


        acc ++: value1
      }


    list

  }

}
