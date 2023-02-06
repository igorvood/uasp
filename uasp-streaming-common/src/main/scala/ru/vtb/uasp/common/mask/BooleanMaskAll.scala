package ru.vtb.uasp.common.mask

import play.api.libs.json.{JsBoolean, JsFalse, JsString}

case class BooleanMaskAll() extends JsBooleanMaskedFun {

  override def mask(in: Boolean): JsBoolean = JsFalse
}
