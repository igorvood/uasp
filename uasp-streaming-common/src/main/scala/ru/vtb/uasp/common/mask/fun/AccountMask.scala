package ru.vtb.uasp.common.mask.fun

import play.api.libs.json.JsString

case class AccountMask() extends JsStringMaskedFun {

  override def mask(in: String): JsString = {
    val maskedDigit = "******"
    JsString(    in.substring(0,10)+maskedDigit+in.substring(16))
  }

}
