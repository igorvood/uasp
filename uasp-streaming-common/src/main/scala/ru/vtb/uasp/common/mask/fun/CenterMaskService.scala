package ru.vtb.uasp.common.mask.fun

import play.api.libs.json.JsString

/** ОБщаяя ф-ция маскирования оставляет в начале cntBegin не маскированых символов и в конце cntEnd не маскированных символов,
 * середина заменяется на *
 * */
case class CenterMaskService(cntBegin: String, cntEnd: String) extends JsStringMaskedFun {
  val cntBeginInt: Int = cntBegin.toInt
  val cntEndInt: Int = cntEnd.toInt

  override def mask(in: String): JsString = {
    val str = in match {
      case s if s == null || s.isEmpty => s
      case s if s.length <= (cntBeginInt + cntEndInt) => (1 to (s.length)).map { _ => "*" }.mkString
      case s =>
        val string = (1 to (s.length - cntBeginInt - cntEndInt)).map { _ => "*" }.mkString
        val beginStr = s.substring(0, cntBeginInt)
        val endStr = s.substring(in.length - cntEndInt)
        val str1 = beginStr + string + endStr
        str1
    }

    JsString(str)
  }
}


