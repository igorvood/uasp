package ru.vtb.uasp.common.mask

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.mask.NewJPath.PathFactory


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

    println(paths)
    assertResult(expected)(paths)


    //    assertResult(root)(path)
  }

  //  "transform str to JPath " should " no ERR when dublicate" in {
  //    val root = JPathObject(rootName,
  //      Set(
  //        JPathObject("f1",
  //          Set(JPathObject("o1", Set(
  //            JPathValue("d1"),
  //            JPathValue("d2"),
  //            JPathValue("d3")
  //          )))),
  //        JPathObject("f2",
  //          Set(
  //            JPathObject("o2", Set(
  //              JPathValue("d3")
  //            )
  //            )
  //          )
  //        )
  //      )
  //    )
  //
  //    val path = List(
  //      "f1.o1.d1",
  //      "f1.o1.d2",
  //      "f1.o1.d3",
  //      "f2.o2.d3",
  //      "f2.o2.d3",
  //    )
  //      .map(MaskedStrPath)
  //      .toJsonPath()
  //    assertResult(root)(path)
  //  }

}
