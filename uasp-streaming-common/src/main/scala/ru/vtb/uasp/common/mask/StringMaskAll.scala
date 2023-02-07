package ru.vtb.uasp.common.mask

import play.api.libs.json.JsString

case class StringMaskAll() extends JsStringMaskedFun {

  override def mask(in: String): JsString = JsString("***MASKED***")

}
