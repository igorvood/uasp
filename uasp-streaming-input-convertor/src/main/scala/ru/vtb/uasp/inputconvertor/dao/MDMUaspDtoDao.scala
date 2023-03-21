package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsError, JsResult, JsSuccess, JsValue}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.getMapEntry
import ru.vtb.uasp.inputconvertor.dao.dto.SysDtoParam
import ru.vtb.uasp.inputconvertor.service.TransformHelper.splitMessage

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object MDMUaspDtoDao {
  def fromJValue(imMessage: JsValue, dtoMap: Map[String, Array[String]], uaspDtoType: String): List[JsResult[UaspDto]] = {
    val expectedSysNumber = uaspDtoType.toUpperCase() match {
      case "MDM" => "OW4"
      case "MDM-PROFILE" => "99995"
    }

    val seq: Seq[JsResult[List[UaspDto]]] = splitMessage[SysDtoParam](imMessage, "contact")
      .map(d1 => {
        d1.map { d =>
          val dtoes = d.cifcontactReference
            .filter(p => p.systemNumber == expectedSysNumber)
            .map(q => {
              val bool = q.is_deleted match {
                case "1" => true
                case _ => false
              }
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
                dataBoolean = Map(getMapEntry(dtoMap("app.uaspdto.fields.is_deleted")(0), bool)),
                uuid = FastUUID.toString(UUID.randomUUID),
                process_timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant.toEpochMilli,

              )
            })
          dtoes
        }
      }
      )
    val seq1 = seq
      .collect { case u: JsSuccess[List[UaspDto]] => u.value }
      .flatten.toList

    val value1 = seq1 match {
      case Nil => List(JsError(s"Unable to parse json for type ${uaspDtoType}"))
      case tl => tl.map(u => JsSuccess(u))
    }
    value1


  }

}
