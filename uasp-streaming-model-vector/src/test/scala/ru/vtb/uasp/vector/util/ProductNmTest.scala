package ru.vtb.uasp.vector.util

import org.scalatest.flatspec.AnyFlatSpec


class ProductNmTest extends AnyFlatSpec {
  "data " should "be equals" in {
    assertResult("Социальная карта Мордовии")(ProductNmUtil.returnProductNm("MRMCEMOR"))
  }

  "data " should "not be" in {
    assertResult("")(ProductNmUtil.returnProductNm("not_exist_key"))
  }

  "ProductNmUtil.getRows " should "be List[String]" in {
    assertResult(386)(ProductNmUtil.getRows.size)
  }
}
