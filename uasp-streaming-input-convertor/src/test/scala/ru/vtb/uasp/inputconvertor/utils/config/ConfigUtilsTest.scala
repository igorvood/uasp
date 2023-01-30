package ru.vtb.uasp.inputconvertor.utils.config

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.utils.config.ConfigUtils.getPropsFromArgs

class ConfigUtilsTest extends AnyFlatSpec with should.Matchers {

  "The getPropsFromArgs" should "be return valid map" in {
    val args = Array[String]("--key1", "val1", "--key2", "val2")
    val argsMap = getPropsFromArgs(args).get
    val standardMap = Map("key1" -> "val1") ++ Map("key2" -> "val2")
    assert(argsMap == standardMap)

  }
}
