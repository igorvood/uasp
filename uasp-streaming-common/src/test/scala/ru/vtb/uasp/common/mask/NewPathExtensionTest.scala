package ru.vtb.uasp.common.mask

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.mask.JsMaskedPath.PathFactory

import scala.util.{Failure, Success, Try}


class NewPathExtensionTest extends AnyFlatSpec with should.Matchers {

  "single transform str to JPath " should " OK" in {
    val expected = JsMaskedPathObject(
      Map("f1" -> JsMaskedPathObject(
        Map("o1" -> JsMaskedPathObject(
          Map("d1" -> JsMaskedPathValue())))))
    )

    val paths = Map("f1.o1.d1" -> "Asdsad")
      .map(q => MaskedStrPathWithFunName(q._1,q._2))
      .toJsonPath()

    assertResult(expected)(paths)


  }

  "two transform not cross str to JPath " should " OK" in {
    val expected = JsMaskedPathObject(
      Map("f1" -> JsMaskedPathObject(
        Map("o1" -> JsMaskedPathObject(
          Map("d1" -> JsMaskedPathValue())))),
        "f2" -> JsMaskedPathObject(
          Map("o1" -> JsMaskedPathObject(
            Map("d1" -> JsMaskedPathValue()))))
      )
    )

    val paths = Map("f1.o1.d1" -> "asdad", "f2.o1.d1"-> "asd")
      .map(q => MaskedStrPathWithFunName(q._1,q._2))
      .toJsonPath()

    assertResult(expected)(paths)


  }


  "two with cross path transform str to JPath " should " OK" in {
    val paths = Map(
      "f1.o1.d1" -> "asd",
      "f1.o1.d2"-> "asdsad",
      //      "f1.o1.d3",
      //      "f2.o2.d3",
    )
      .map(q => MaskedStrPathWithFunName(q._1,q._2))
      .toJsonPath()

    val expected = JsMaskedPathObject(
      Map("f1" -> JsMaskedPathObject(
        Map("o1" -> JsMaskedPathObject(
          Map(
            "d1" -> JsMaskedPathValue(),
            "d2" -> JsMaskedPathValue(),
          )))))
    )

    assertResult(expected)(paths)
  }

  "two with across error register object " should " OK" in {
    val paths1 = Map(
      "f1.o1.d1" -> "ASdasd",
      "f1.o1.d1.q1"-> "Asdsad",
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
      "f1.o1.d1.q1" -> "asdf",
      "f1.o1.d1"-> "dasdasd",
    )
      .map(q => MaskedStrPathWithFunName(q._1,q._2))

    Try(paths1
      .toJsonPath())       match {
      case Success(_) => throw new RuntimeException("must fail")
      case Failure(exception) => assertResult("Wrong structure 'd1' it is value, but 'Set(d1)' all ready registered like object")(exception.getMessage)
    }

  }

}
