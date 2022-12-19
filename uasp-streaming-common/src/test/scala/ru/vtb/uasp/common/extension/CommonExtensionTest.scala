package ru.vtb.uasp.common.extension

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import ru.vtb.uasp.common.extension.CommonExtension.Also

class CommonExtensionTest extends AnyFlatSpec with should.Matchers {

  "also, function value" should "be called" in {
    val alsoObject = new Also(1)
    var cnt = 0
    val res = alsoObject.also(n => {
      cnt = cnt + 1
      n.toString
    })

    res should be("1")
    cnt should be(1)
  }


}
