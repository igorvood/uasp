package ru.vtb.uasp.common.mask

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.mask.MaskedPredef.MaskJsValuePredef
import ru.vtb.uasp.common.mask.dto.JsMaskedPathObject
import ru.vtb.uasp.common.mask.dto.{JsNumberMaskedPathValue, JsStringMaskedPathValue}
import ru.vtb.uasp.common.mask.fun.{NumberMaskAll, StringMaskAll}

import scala.util.{Failure, Success, Try}


class PathExtensionTest extends AnyFlatSpec with should.Matchers {

  private val jsStringMaskedPathValue=  JsStringMaskedPathValue(StringMaskAll())
  private val jsNumberMaskedPathValue=  JsNumberMaskedPathValue(NumberMaskAll())

  "single transform str to JPath " should " OK" in {
    val expected = JsMaskedPathObject(
      Map("f1" -> JsMaskedPathObject(
        Map("o1" -> JsMaskedPathObject(
          Map("d1" -> jsStringMaskedPathValue)))))
    )

    val paths = Map("f1.o1.d1" -> "ru.vtb.uasp.common.mask.fun.StringMaskAll")
      .map(q => MaskedStrPathWithFunName(q._1,q._2))
      .toJsonPath()

    assertResult(expected)(paths.right.get)


  }

  "two transform not cross str to JPath " should " OK" in {
    val expected = JsMaskedPathObject(
      Map("f1" -> JsMaskedPathObject(
        Map("o1" -> JsMaskedPathObject(
          Map("d1" -> jsStringMaskedPathValue)))),
        "f2" -> JsMaskedPathObject(
          Map("o1" -> JsMaskedPathObject(
            Map("d1" -> jsNumberMaskedPathValue))))
      )
    )

    val paths = Map(
      "f1.o1.d1" -> "ru.vtb.uasp.common.mask.fun.StringMaskAll",
      "f2.o1.d1"-> "ru.vtb.uasp.common.mask.fun.NumberMaskAll")
      .map(q => MaskedStrPathWithFunName(q._1,q._2))
      .toJsonPath()

    assertResult(expected)(paths.right.get)


  }


  "two with cross path transform str to JPath " should " OK" in {
    val paths = Map(
      "f1.o1.d1" -> "ru.vtb.uasp.common.mask.fun.StringMaskAll",
      "f1.o1.d2"-> "ru.vtb.uasp.common.mask.fun.StringMaskAll",
      //      "f1.o1.d3",
      //      "f2.o2.d3",
    )
      .map(q => MaskedStrPathWithFunName(q._1,q._2))
      .toJsonPath()

    val expected = JsMaskedPathObject(
      Map("f1" -> JsMaskedPathObject(
        Map("o1" -> JsMaskedPathObject(
          Map(
            "d1" -> jsStringMaskedPathValue,
            "d2" -> jsStringMaskedPathValue,
          )))))
    )

    assertResult(expected)(paths.right.get)
  }

  "two with across error register object " should " OK" in {
    val paths1 = Map(
      "f1.o1.d1" -> "ru.vtb.uasp.common.mask.fun.StringMaskAll",
      "f1.o1.d1.q1"-> "ru.vtb.uasp.common.mask.fun.StringMaskAll",
    )
      .map(q => MaskedStrPathWithFunName(q._1,q._2))

    Try(paths1
      .toJsonPath())       match {
      case Success(_) => throw new RuntimeException("must fail")
      case Failure(exception) => assertResult("Wrong structure 'd1.q1' it is object, but 'd1' all ready registered like value")(exception.getMessage)
    }

  }

  "two with across error register value " should " OK" in {
    val paths1 = Map(
      "f1.o1.d1.q1" -> "ru.vtb.uasp.common.mask.fun.StringMaskAll",
      "f1.o1.d1"-> "ru.vtb.uasp.common.mask.fun.StringMaskAll",
    )
      .map(q => MaskedStrPathWithFunName(q._1,q._2))

    Try(paths1
      .toJsonPath())       match {
      case Success(_) => throw new RuntimeException("must fail")
      case Failure(exception) => assertResult("Wrong structure 'd1' it is value, but 'Set(d1)' all ready registered like object")(exception.getMessage)
    }

  }

}
