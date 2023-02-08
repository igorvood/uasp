package ru.vtb.uasp.common.mask.fun

import play.api.libs.json.JsString

/** Адрес регистрации:
 * удалить последние пять символов строки и заменять их на три «звездочки».
 */
case class AddressMaskService() extends JsStringMaskedFun {

  override def mask(in: String): JsString = {
    JsString(in.substring(0, in.length - 5) + "***")
  }


}
