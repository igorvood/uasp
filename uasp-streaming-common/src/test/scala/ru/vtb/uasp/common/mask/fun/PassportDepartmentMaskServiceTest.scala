package ru.vtb.uasp.common.mask.fun

import org.scalatest.flatspec.AnyFlatSpec

import scala.collection.immutable

class PassportDepartmentMaskServiceTest extends AnyFlatSpec {

  val maskService = PassportDepartmentMaskService()


  val testCases = Map(
    "Отделом милиции №12 УВД №12 г. Петропавлоск-Камчатский" -> "Отделом милиц ***** лоск-Камчатский",
  )

  "mask all existing fields " should " OK" in {

    val value: immutable.Iterable[Either[String, (String, String)]] = testCases.map(tc => {

      val maskedJsString = maskService.mask(tc._1)
      val stringOrTuple = maskedJsString.value match {
        case tc._2 => Right(tc)
        case _ => Left(s"value '${tc._1}' must be masked like '${tc._2}' but actual is '${maskedJsString.value}'")
      }
      stringOrTuple
    })
    val errors = value
      .collect { case l: Left[String, (String, String)] => l.value }
      .mkString("\n")
    assertResult("")(errors)
  }
}
