package ru.vtb.uasp.common.mask.fun

import play.api.libs.json.JsString

/** Для фамилии имеющей тире или пробел в написании, независимо от длины составных частей удалять все символы,кроме первого символа, разделяющего символа(тире или пробела) и символа, следующего
  * за разделяющим символом. Вместо удалённых символов показывать три «звездочки» всегда. Пример:Салтыков-Щедрин-> C***-Щ***.b.
  * Для фамилии, имеющей свыше 7-ми символов – удалять все знаки,кроме первого и двух последних, вместо удалённых символов показывать три «звездочки» всегда. Пример:Смирнова-> C***ва.c.
  * Для фамилии, имеющей от 5-ти до 7-ми символов,удалять всезнаки кроме первого, вместо удалённых символов показывать «звездочки» по количеству удалённых символов.Пример:Иванов -> И*****.d.
  * Для фамилии длинной до 5-ти символов, удалять все символы, вместо удалённых символов показывать «звездочки» по количеству удалённых символов. Пример:Ли-> **
* */
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
