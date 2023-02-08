package ru.vtb.uasp.common.mask.fun

import play.api.libs.json.JsString

/** Контактный телефон – маскируются все символы кроме двух последних и кода страны в формате . Пример: +7 *** *****45.
 */
case class PhoneStrMaskService() extends JsStringMaskedFun {

  override def mask(in: String): JsString = {
    val i = in.length - 4
    val inclusive = 0 to i
    val string = inclusive.map(a => "*").mkString
    JsString(in.substring(0, 2)+string + in.substring(in.length-2))
  }



}
