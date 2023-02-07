package ru.vtb.uasp.common.mask.fun

import play.api.libs.json.JsString

case class PassportDepartmentMaskService() extends JsStringMaskedFun {

  override def mask(in: String): JsString = {
    val i = in.length / 4
    JsString(in.substring(0, i)+" ***** "+in.substring(i*3))
  }



}
