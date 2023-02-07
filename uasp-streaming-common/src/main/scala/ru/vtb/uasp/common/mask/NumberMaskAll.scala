package ru.vtb.uasp.common.mask

import play.api.libs.json.JsNumber

case class NumberMaskAll() extends JsNumberMaskedFun {

  override def mask(in: BigDecimal): JsNumber = JsNumber(0)

}
