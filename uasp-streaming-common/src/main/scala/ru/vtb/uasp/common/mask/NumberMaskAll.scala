package ru.vtb.uasp.common.mask

import play.api.libs.json.JsString

class NumberMaskAll extends JsStringMaskedFun{
  override def mask(v: JsString): JsString = JsString("***MASKED***")
}
