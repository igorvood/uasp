package tests.Util.file

import org.scalatest.flatspec.AnyFlatSpec
import ru.vtb.uasp.pilot.model.vector.constants.ProductNm


class FileUtilsTest extends AnyFlatSpec {

  "data " should "be equals" in {
    assertResult("Социальная карта Мордовии")(ProductNm.returnProductNm("MRMCEMOR"))
  }

  "data " should "not be" in {
    assertResult("")(ProductNm.returnProductNm("not_exist_key"))
  }
}
