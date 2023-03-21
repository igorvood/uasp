package ru.vtb.uasp.common.mask.fun
import play.api.libs.json.JsString

/**
 * Маскируются 6 символов начиная с пятого с права, пример: 123***************1234
 */
case class AccountMask() extends JsStringMaskedFun{
  private val service: CenterMaskService = CenterMaskService("3", "4")
  override def mask(in: String): JsString = service.mask(in)
}