package ru.vtb.uasp.common.mask.fun

import play.api.libs.json.JsString

case class PassportNumberInStrMaskService() extends JsStringMaskedFun {

  override def mask(in: String): JsString =
    JsString(in.substring(0, 4)+"***"+in.substring(7))

}
