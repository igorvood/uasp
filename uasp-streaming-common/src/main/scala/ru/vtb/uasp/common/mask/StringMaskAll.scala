package ru.vtb.uasp.common.mask

import play.api.libs.json.{JsNumber, JsString}

class StringMaskAll extends JsNumberMaskedFun{

  override def mask(v: JsNumber): JsNumber = JsNumber(-1)
}
