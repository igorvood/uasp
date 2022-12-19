package ru.vtb.uasp.mutator.way4

import io.qameta.allure.Feature
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.mutator.service.BusinessRulesService
import ru.vtb.uasp.mutator.service.BusinessRulesService.errFieldName
import ru.vtb.uasp.mutator.service.dto.{ConcatenateStr, StringMap, UaspOperation}
import ru.vtb.uasp.mutator.way4.ConstTest.{addKey, drlFileListConst, someValue}
import ru.vtb.uasp.mutator.way4.Way4Cases5_3Test.{testData, testcaseNumber, typeOfCase5_3, validUaspDto5_3}
import ru.vtb.uasp.mutator.way4.abstraction.{AbstractDroolsTestCase, AddTestAction, DeleteTestAction, NoneTestAction, TestBigDecimal, TestCaseData, TestLong, TestString}

import scala.util.Try


@Feature("Way4Cases 5.3 Test")
class Way4Cases5_3Test extends AbstractDroolsTestCase {

  override protected def drlFileList: List[String] = drlFileListConst

  "Way4Cases " + testcaseNumber should " be ok, no field " + addKey in {

    val triedAssertions = testData().map { test =>
      test -> Try {
        val result = runDrools(validUaspDto5_3, test).toList
        test.expecped.map { q =>
          assertResult(1)(result.size)
          assertResult(result.head)(UaspOperation("5.3", StringMap(q), addKey, ConcatenateStr(",")))
        }.getOrElse(assertResult(0)(result.size))
      }
    }
    assertTry(triedAssertions)
  }

  "Way4Cases " + testcaseNumber should " be ok, no field " + addKey + " check all BusinessRulesService" in {

    val businessRulesService = BusinessRulesService(List("way4-case-2_2.drl", "way4-case-2_3.drl", "way4-case-2_4.drl", "way4-case-2_10.drl", "way4-case-5_2.drl", "way4-case-5_3.drl", "way4-case-11_2.drl"))
    val triedAssertions = testData().map { test =>
      test -> Try {
        val testDto = modifyTestData(validUaspDto5_3, test)
        val mutatingDto = businessRulesService.map(testDto)

        test.expecped.map { q =>
          val caseName = mutatingDto.dataString.get(addKey)
          assertResult(typeOfCase5_3)(caseName)
          assertResult(testDto)(mutatingDto.copy(dataString = mutatingDto.dataString - addKey, process_timestamp = testDto.process_timestamp))
        }.getOrElse(assertResult(testDto)(mutatingDto.copy(dataString = mutatingDto.dataString, process_timestamp = testDto.process_timestamp)))
      }
    }
    assertTry(triedAssertions)
  }

  "Way4Cases " + testcaseNumber should " be some times error, with field " + addKey + " check all BusinessRulesService" in {

    val businessRulesService = BusinessRulesService(List("way4-case-2_2.drl", "way4-case-2_3.drl", "way4-case-2_4.drl", "way4-case-2_10.drl", "way4-case-5_2.drl", "way4-case-5_3.drl", "way4-case-11_2.drl"))
    val triedAssertions = testData()
      .map { test =>
        val dtoWithClassification = validUaspDto5_3.copy(dataString = validUaspDto5_3.dataString + (addKey -> someValue))
        val testDto = modifyTestData(dtoWithClassification, test)
        val triedBoolean = Try {
          val mutatingDto = businessRulesService.map(testDto)

          test.expecped.map { q =>
            val dto = testDto.copy(dataString = testDto.dataString + (addKey -> (someValue + "," + typeOfCase5_3.value)))
            assertResult(dto)(mutatingDto.copy(dataString = mutatingDto.dataString - errFieldName, process_timestamp = dto.process_timestamp))
            assertResult(None)(mutatingDto.dataString.get(errFieldName))
          }.getOrElse({
            assertResult(testDto)(mutatingDto.copy(dataString = mutatingDto.dataString, process_timestamp = testDto.process_timestamp))
          })
        }
        test -> triedBoolean
      }

    assertTry(triedAssertions)
  }

}

object Way4Cases5_3Test {
  val validUaspDto5_3: UaspDto = UaspDto(
    "validUaspId",
    Map(),
    Map("transaction_datetime" -> 1512),
    Map(),
    Map(),
    Map("transaction_amount" -> BigDecimal(152)),
    Map(
      "action_type" -> "Presentment",
      "processing_resolution" -> "Accepted",
      "payment_direction" -> "Credit",
      "terminal_type" -> "~",
      "local_id" -> "way4Id",
      "audit_auth_code" -> "audit_auth_code",
      "audit_rrn" -> "audit_rrn",
      "audit_srn" -> "audit_srn",

    ),
    Map(),
    "validUaspUUID",
    13
  )

  private def testcaseNumber = "5-3"

  private def typeOfCase5_3: Some[String] = Some(s"way4-case-$testcaseNumber")

  protected def testData(): List[TestCaseData] = List[TestCaseData](
    TestString("action_type", NoneTestAction(), typeOfCase5_3),
    TestString("action_type", AddTestAction("qwer")),
    TestString("action_type", DeleteTestAction()),

    TestString("processing_resolution", DeleteTestAction()),
    TestString("processing_resolution", AddTestAction("qwer")),

    TestString("payment_direction", AddTestAction("qwer")),
    TestString("payment_direction", DeleteTestAction()),

    TestString("terminal_type", AddTestAction("WEB")),
    TestString("terminal_type", AddTestAction("ECOMMERCE")),
    TestString("terminal_type", DeleteTestAction()),

    TestLong("transaction_datetime", DeleteTestAction()),

    TestBigDecimal("transaction_amount", DeleteTestAction()),

    TestString("local_id", DeleteTestAction()),

    TestString("audit_auth_code", DeleteTestAction()),

    TestString("audit_rrn", DeleteTestAction()),

    TestString("audit_srn", DeleteTestAction()),

  )

}


