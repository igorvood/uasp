package ru.vtb.uasp.common.mask

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.mask.NewJPath.PathFactory

import scala.util.{Failure, Success, Try}


class NewPathExtensionTest extends AnyFlatSpec with should.Matchers {

  "single transform str to JPath " should " OK" in {
    val expected = NewJPathObject(
      Map("f1" -> NewJPathObject(
        Map("o1" -> NewJPathObject(
          Map("d1" -> NewJPathValue())))))
    )

    val paths = List("f1.o1.d1")
      .map(MaskedStrPath)
      .toJsonPath18()

    assertResult(expected)(paths)


  }

  "two transform not cross str to JPath " should " OK" in {
    val expected = NewJPathObject(
      Map("f1" -> NewJPathObject(
        Map("o1" -> NewJPathObject(
          Map("d1" -> NewJPathValue())))),
        "f2" -> NewJPathObject(
          Map("o1" -> NewJPathObject(
            Map("d1" -> NewJPathValue()))))
      )
    )

    val paths = List("f1.o1.d1", "f2.o1.d1")
      .map(MaskedStrPath)
      .toJsonPath18()

    assertResult(expected)(paths)


  }


  "two with cross path transform str to JPath " should " OK" in {
    val paths = List(
      "f1.o1.d1",
      "f1.o1.d2",
      //      "f1.o1.d3",
      //      "f2.o2.d3",
    )
      .map(MaskedStrPath)
      .toJsonPath18()

    val expected = NewJPathObject(
      Map("f1" -> NewJPathObject(
        Map("o1" -> NewJPathObject(
          Map(
            "d1" -> NewJPathValue(),
            "d2" -> NewJPathValue(),
          )))))
    )

    assertResult(expected)(paths)
  }

  "two with across error register object " should " OK" in {
    val paths1 = List(
      "f1.o1.d1",
      "f1.o1.d1.q1",
    )
      .map(MaskedStrPath)

    Try(paths1
      .toJsonPath18())       match {
      case Success(_) => throw new RuntimeException("must fail")
      case Failure(exception) => assertResult("Wrong structure 'd1.q1' it is object, but 'd1' all ready registered like value")(exception.getMessage)
    }

  }

  "two with across error register value " should " OK" in {
    val paths1 = List(
      "f1.o1.d1.q1",
      "f1.o1.d1",
    )
      .map(MaskedStrPath)

    Try(paths1
      .toJsonPath18())       match {
      case Success(_) => throw new RuntimeException("must fail")
      case Failure(exception) => assertResult("Wrong structure 'd1' it is value, but 'Set(d1)' all ready registered like object")(exception.getMessage)
    }

  }

}
