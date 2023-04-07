package ru.vtb.uasp.inputconvertor.dao

import com.eatthepath.uuid.FastUUID
import play.api.libs.json.{JsResult, JsValue, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonDao.{getMapEntry, mapCollect}
import ru.vtb.uasp.inputconvertor.dao.dto.{CloseAccountDto, WithdrawUaspDto}

import java.time.{LocalDateTime, ZoneId}
import java.util.UUID

object CloseAccountDao {

  lazy val timeZone = "Europe/Moscow"

  def fromJValue(inMessage: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] = {
    val closeAccountDTo = inMessage.validate[CloseAccountDto]
      .map(d => Json.toJson(d))
    List(closeAccountDTo)
  }

}
