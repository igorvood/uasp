package ru.vtb.uasp.common.mask.fun

import play.api.libs.json.JsString

/** Серия и номер паспорта: маскировать первые три цифры номера(т.е.с 5-гопо7-йсимволы):
 * */
case class PassportNumberInStrMaskService() extends JsStringMaskedFun {

  override def mask(in: String): JsString =
    JsString(in.substring(0, 4) + "***" + in.substring(7))

}
