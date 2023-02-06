package ru.vtb.uasp.common.extension

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.mask.JPath.PathFactory
import ru.vtb.uasp.common.mask.{JPathObject, JPathValue, MaskedStrPath}


class PathExtensionTest extends AnyFlatSpec with should.Matchers {

  "transform str to JPath " should " OK" in {
    val root = JPathObject("root",
      Set(
        JPathObject("f1",
          Set(JPathObject("o1", Set(
            JPathValue("d1"),
            JPathValue("d2"),
            JPathValue("d3")
          )))),
        JPathObject("f2",
          Set(
            JPathObject("o2", Set(
              JPathValue("d3")
            )
            )
          )
        )
      )
    )

    val path = List(
      "f1.o1.d1",
      "f1.o1.d2",
      "f1.o1.d3",
      "f2.o2.d3",
    )
      .map(MaskedStrPath)
      .toJsonPath()

    assertResult(root)(path)


  }

}
