package ru.vtb.uasp.common.extension

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.mask.JPath.PathFactory
import ru.vtb.uasp.common.mask.{JPath, JPathObject, JPathValue}


class PathExtensionTest extends AnyFlatSpec with should.Matchers {

  private val pathObject: JPath = JPathObject("root",
    Set(
      JPathObject("f1", Set(JPathObject("o1", Set(JPathValue("d1"), JPathValue("d2"), JPathValue("d3"))))),
      JPathObject("f2", Set(JPathObject("o2", Set(JPathValue("d3")))))
    )
  )

  "print " should "be called" in {


    val root = JPathObject("root",
      Set(
        JPathObject("f1", Set(JPathObject("o1",
          Set(JPathValue("d1"),
            JPathValue("d2"),
            JPathValue("d3")
          )))),
        JPathObject("f2", Set(JPathObject("o2", Set(JPathValue("d3")))))
      )
    )

    val path = List(
      "f1.o1.d1",
      "f1.o1.d2",
      "f1.o1.d3",
      "f2.o2.d3",
    ).toJsonPath()

    assertResult(root)(path)


  }

  "also, function value" should "be called" in {
    val paths = List("f1.o1.d1",
      "f1.o1.d2",
      "f1.o1.d3",
      "f2.o2.d3",
    )

    //    val strings = "f2.o2.d3".split("\\.")

    val path1 = paths.toJsonPath()
    assertResult(pathObject)(path1)

  }


}
