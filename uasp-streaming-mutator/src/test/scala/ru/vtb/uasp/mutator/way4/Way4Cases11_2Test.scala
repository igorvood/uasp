package ru.vtb.uasp.mutator.way4

import io.qameta.allure.Feature
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.mutator.service.BusinessRulesService.errFieldName
import ru.vtb.uasp.mutator.service.dto.{ConcatenateStr, StringMap, UaspOperation}
import ru.vtb.uasp.mutator.way4.ConstTest.{addKey, drlFileListConst, someValue}
import ru.vtb.uasp.mutator.way4.Way4Cases11_2Test.{caseNumber, testData, typeOfCase11_2, validUaspDto11_2}
import ru.vtb.uasp.mutator.way4.abstraction._

import scala.util.Try

@Feature("Way4Cases 11.2 Test")
class Way4Cases11_2Test extends AbstractDroolsTestCase {

  override protected def drlFileList: List[String] = drlFileListConst

  "Way4Cases " + caseNumber should " be ok" in {
    val triedAssertions = testData().map { test =>
      test -> Try {
        val result = runDrools(validUaspDto11_2, test).toList
        test.expecped.map { q =>
          assertResult(1)(result.size)
          assertResult(result.head)(UaspOperation("11.2", StringMap(q), addKey, ConcatenateStr(",")))
        }.getOrElse(assertResult(0)(result.size))
      }
    }
    assertTry(triedAssertions)
  }

  "Way4Cases " + caseNumber should " be ok, no field " + addKey + " check all BusinessRulesService" in {

    val triedAssertions = testData().map { test =>
      test -> Try {
        val testDto = modifyTestData(validUaspDto11_2, test)
        val mutatingDto = businessRulesService.processWithDlq(testDto)
        val rightUasp = mutatingDto.right.get
        val assertion = test.expecped.map { q =>
          val caseName = rightUasp.dataString.get(addKey)
          assertResult(typeOfCase11_2)(caseName)
          assertResult(testDto)(rightUasp.copy(dataString = rightUasp.dataString - addKey, process_timestamp = testDto.process_timestamp))
        }.getOrElse(assertResult(testDto)(rightUasp.copy(dataString = rightUasp.dataString, process_timestamp = testDto.process_timestamp)))
        assertion
      }
    }
    assertTry(triedAssertions)
  }

  "Way4Cases " + caseNumber should " be some times error, with field " + addKey + " check all BusinessRulesService" in {

    val triedAssertions = testData()
      .map { test =>
        val dtoWithClassification = validUaspDto11_2.copy(dataString = validUaspDto11_2.dataString + (addKey -> someValue))
        val testDto = modifyTestData(dtoWithClassification, test)
        val triedBoolean = Try {
          val mutatingDto = businessRulesService.processWithDlq(testDto)
          val rightUasp = mutatingDto.right.get
          test.expecped.map { q =>
            assertResult(testDto.copy(dataString = testDto.dataString + (addKey -> (someValue + "," + typeOfCase11_2.value))))(rightUasp.copy(dataString = rightUasp.dataString - errFieldName))
            assertResult(None)(rightUasp.dataString.get(errFieldName))
          }.getOrElse({
            assertResult(testDto)(rightUasp.copy(dataString = rightUasp.dataString, process_timestamp = testDto.process_timestamp))
          })
        }
        test -> triedBoolean
      }

    assertTry(triedAssertions)
  }
}


object Way4Cases11_2Test {

  val validUaspDto11_2: UaspDto = UaspDto(
    "validUaspId",
    Map(),
    Map("transaction_datetime" -> 1512),
    Map(),
    Map(),
    Map("transaction_amount" -> BigDecimal(152)),
    Map(
      "operation_id" -> "operation_id",
      "local_id" -> "way4Id",
      "action_type" -> "Presentment",
      "processing_resolution" -> "Accepted",
      "payment_direction" -> "Credit",
    ),
    Map("is_mortgage" -> true),
    "validUaspUUID",
    16
  )

  private def caseNumber = "11-2"

  private def typeOfCase11_2: Some[String] = Some(s"way4-case-$caseNumber")

  protected def testData(): List[TestCaseData] = List[TestCaseData](
    TestString("action_type", NoneTestAction(), typeOfCase11_2),

    TestString("operation_id", DeleteTestAction()),

    TestString("local_id", DeleteTestAction()),

    TestLong("transaction_datetime", DeleteTestAction()),

    TestString("action_type", AddTestAction("qwer")),
    TestString("action_type", DeleteTestAction()),

    TestString("processing_resolution", DeleteTestAction()),
    TestString("processing_resolution", AddTestAction("qwer")),

    TestString("payment_direction", AddTestAction("qwer")),
    TestString("payment_direction", DeleteTestAction()),

    TestBoolean("is_mortgage", AddTestAction("false")),
    TestBoolean("is_mortgage", DeleteTestAction()),

  )

}




