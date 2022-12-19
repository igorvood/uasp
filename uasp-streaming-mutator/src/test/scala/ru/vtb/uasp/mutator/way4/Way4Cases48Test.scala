package ru.vtb.uasp.mutator.way4

import io.qameta.allure.Feature
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.mutator.service.BusinessRulesService
import ru.vtb.uasp.mutator.service.BusinessRulesService.errFieldName
import ru.vtb.uasp.mutator.service.dto.{Add, ConcatenateStr, StringMap, UaspOperation}

import ru.vtb.uasp.mutator.way4.Way4Cases48Test.{caseNumber, testData, typeOfCase48, validUaspDto48}
import ru.vtb.uasp.mutator.way4.abstraction._

import scala.util.Try

@Feature("Way4Cases 48 Test")
class Way4Cases48Test extends AbstractDroolsTestCase {

  val addKey = "customer_id_and_masked_card_number"

  override protected def drlFileList: List[String] = List("way4-case-48-concatenate.drl")

  "Way4Cases " + caseNumber should " be ok" in {
    val triedAssertions = testData().map { test =>
      test -> Try {
        val result = runDrools(validUaspDto48, test).toList
        test.expecped.map { q =>
          assertResult(1)(result.size)

          assertResult(UaspOperation("48", StringMap(q), addKey, Add()))(result.head)
        }.getOrElse(assertResult(0)(result.size))
      }
    }
    assertTry(triedAssertions)
  }

}


object Way4Cases48Test {

  val validUaspDto48: UaspDto = UaspDto(
    "validUaspId",
    Map(),
    Map(),
    Map(),
    Map(),
    Map(),
    Map(
      "calculate-mdm_id" -> "masked_card_number",
      "card_masked_pan" -> "customer_id",
    ),
    Map(),
    "validUaspUUID",
    16
  )

  private def caseNumber = "48"

  private def typeOfCase48: Some[String] = Some(s"customer_id_and_masked_card_number")

  protected def testData(): List[TestCaseData] = List[TestCaseData](
    TestString("masked_card_number", NoneTestAction(), Some("masked_card_number;customer_id")),

    TestString("calculate-mdm_id", DeleteTestAction()),
    TestString("card_masked_pan", DeleteTestAction()),


  )

}




