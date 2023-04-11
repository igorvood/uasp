package ru.vtb.uasp.common.mask.fun

import play.api.libs.json.JsString

/**
 * Маскируются все до запятой
 */
case class SummaMaskService() extends JsStringMaskedFun {


  override def mask(in: String): JsString = {
    val strings = in.split(",")

    val result = if (strings.length == 1)
      "*"
    else if (strings.length > 1)
      "*," + strings.tail.mkString(",")
    else ""
    JsString(result)
  }
}