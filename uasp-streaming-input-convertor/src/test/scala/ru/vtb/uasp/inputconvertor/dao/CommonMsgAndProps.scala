package ru.vtb.uasp.inputconvertor.dao

import play.api.libs.json.{JsValue, Json}
import ru.vtb.uasp.common.utils.config.ConfigUtils.getStringFromResourceFile

object CommonMsgAndProps {

  def jsValueByType(uaspdtoType: String): JsValue = {

    val jsonMessageStr = getStringFromResourceFile(uaspdtoType + "-test.json")

    Json.parse(jsonMessageStr)

  }


}
