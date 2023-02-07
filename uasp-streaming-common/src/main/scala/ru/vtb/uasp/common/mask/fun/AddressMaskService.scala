package ru.vtb.uasp.common.mask.fun

import play.api.libs.json.JsString

case class AddressMaskService() extends JsStringMaskedFun {

  override def mask(in: String): JsString = {
    JsString(in.substring(0, in.length - 5) + "***")
  }


}
