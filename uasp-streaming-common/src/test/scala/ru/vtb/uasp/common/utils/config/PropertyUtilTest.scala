package ru.vtb.uasp.common.utils.config

import org.scalatest.flatspec.AnyFlatSpec
import ru.vtb.uasp.common.utils.config.PropertyUtil._

class PropertyUtilTest extends AnyFlatSpec {

  it should " extractNamesPlaceholder ok" in {
    val strings = extractNamesPlaceholder("${PH_1}_${PH_2}_${PH_1}")

    assertResult(List("PH_1", "PH_2", "PH_1"))(strings)

  }

  it should " replaceDifficultPlaceHolders ok" in {

    SomeClassName.resolvablePlaceHolders.clear()
    SomeClassName.resolvablePlaceHolders.put("PH_1", "val1")
    SomeClassName.resolvablePlaceHolders.put("PH_2", "val2")

    implicit val name = SomeClassName

    val strings = replaceDifficultPlaceHolders("${PH_1}_${PH_2}_${PH_1}")

    assertResult("val1_val2_val1")(strings)

  }


  it should " replaceDifficultPlaceHolders part resolve " in {
    SomeClassName.resolvablePlaceHolders.clear()
    SomeClassName.resolvablePlaceHolders.put("PH_1", "val1")

    implicit val name = SomeClassName
    val strings = replaceDifficultPlaceHolders("SOME_CONST_${PH_1}_${PH_2}_${PH_1}_end_const")

    assertResult("SOME_CONST_val1_${PH_2}_val1_end_const")(strings)

  }

}
