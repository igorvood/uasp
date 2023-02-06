package ru.vtb.uasp.common.mask

import play.api.libs.json.JsNumber

class NumberMaskAll extends JsNumberMaskedFun {

  override def apply(v1: JsNumber): JsNumber = JsNumber(0)

}
