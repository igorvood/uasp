package ru.vtb.uasp.common.mask.fun

import play.api.libs.json.JsString

case class NameMaskService() extends JsStringMaskedFun {

  override def mask(in: String): JsString = {
    val difficultNameDef = in.split("-")
    val difficultNameDefSpace = in.split(" ")

    val parsed = if (difficultNameDef.length > 1) {
      difficultNameDef -> Some("-")
    } else if (difficultNameDefSpace.length > 1) {
      difficultNameDefSpace -> Some(" ")
    } else (Array(in) -> None)


    val value = parsed._2
      .map(delimer => {
        parsed._1.toList
          .map(n =>
            n(0) + (0 until 3).map(_ => "*").mkString
          ).mkString(delimer)
      })
      .getOrElse {
        val maskedSimpleName = in match {
          case n if n.length > 7 => n(0) + "***" + n.substring(n.length - 2)
          case n if n.length >= 5 => n(0) + (0 until n.length - 1).map(_ => "*").mkString
          case n => (0 until n.length).map(_ => "*").mkString
        }
        maskedSimpleName
      }
    JsString(value)
  }

}
