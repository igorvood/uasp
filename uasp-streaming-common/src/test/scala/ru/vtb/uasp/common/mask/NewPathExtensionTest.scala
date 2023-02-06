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

    val paths = Map("f1.o1.d1" -> "Asdsad")
      .map(q => MaskedStrPath(q._1,q._2))
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

    val paths = Map("f1.o1.d1" -> "asdad", "f2.o1.d1"-> "asd")
      .map(q => MaskedStrPath(q._1,q._2))
      .toJsonPath18()

    assertResult(expected)(paths)


  }


  "two with cross path transform str to JPath " should " OK" in {
    val paths = Map(
      "f1.o1.d1" -> "asd",
      "f1.o1.d2"-> "asdsad",
      //      "f1.o1.d3",
      //      "f2.o2.d3",
    )
      .map(q => MaskedStrPath(q._1,q._2))
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
    val paths1 = Map(
      "f1.o1.d1" -> "ASdasd",
      "f1.o1.d1.q1"-> "Asdsad",
    )
      .map(q => MaskedStrPath(q._1,q._2))

    Try(paths1
      .toJsonPath18())       match {
      case Success(_) => throw new RuntimeException("must fail")
      case Failure(exception) => assertResult("Wrong structure 'd1.q1' it is object, but 'd1' all ready registered like value")(exception.getMessage)
    }

  }

  "two with across error register value " should " OK" in {
    val paths1 = Map(
      "f1.o1.d1.q1" -> "asdf",
      "f1.o1.d1"-> "dasdasd",
    )
      .map(q => MaskedStrPath(q._1,q._2))

    Try(paths1
      .toJsonPath18())       match {
      case Success(_) => throw new RuntimeException("must fail")
      case Failure(exception) => assertResult("Wrong structure 'd1' it is value, but 'Set(d1)' all ready registered like object")(exception.getMessage)
    }

  }

}
