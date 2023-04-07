package ru.vtb.uasp.inputconvertor.dao

import play.api.libs.json.{JsResult, JsValue, Json}
import ru.vtb.uasp.inputconvertor.dao.dto.CloseAccountDto

object CloseAccountDao {

  lazy val timeZone = "Europe/Moscow"

  def fromJValue(inMessage: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[JsValue]] = {
    val closeAccountDTo = inMessage.validate[CloseAccountDto]
      .map(d => Json.toJson(d))
    List(closeAccountDTo)
  }

}
