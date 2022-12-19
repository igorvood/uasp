package ru.vtb.uasp.mutator.way4

import io.qameta.allure.Feature
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.mutator.service.BusinessRulesService
import ru.vtb.uasp.mutator.service.BusinessRulesService.errFieldName
import ru.vtb.uasp.mutator.service.dto.{ConcatenateStr, StringMap, UaspOperation}
import ru.vtb.uasp.mutator.way4.ConstTest.{addKey, drlFileListConst, someValue}
import ru.vtb.uasp.mutator.way4.Way4Cases2_10Test._
import ru.vtb.uasp.mutator.way4.abstraction.{AbstractDroolsTestCase, AddTestAction, DeleteTestAction, NoneTestAction, TestBigDecimal, TestCaseData, TestLong, TestString}

import scala.util.Try


@Feature("Way4Cases 2.10 Test")
class Way4Cases2_10Test extends AbstractDroolsTestCase {

  override protected def drlFileList: List[String] = drlFileListConst

  "Way4Cases " + testcasePostfix should " be ok, no field " + addKey in {

    val triedAssertions = testData().map { test =>
      test -> Try {
        val result = runDrools(validUaspDto2_10, test).toList
        test.head.expecped.map { q =>
          assertResult(1)(result.size)
          assertResult(result.head)(UaspOperation("2.10", StringMap(q), addKey, ConcatenateStr(",")))
        }.getOrElse(assertResult(0)(result.size))
      }
    }
    assertTry(triedAssertions)
  }

  "Way4Cases " + testcasePostfix should " be ok, no field " + addKey + " check all BusinessRulesService" in {

    val triedAssertions = testData().map { test =>
      test -> Try {
        val testDto = modifyListTestData(validUaspDto2_10, test)
        val mutatingDto = businessRulesService.map(testDto)

        test.head.expecped.map { q =>
          val caseNameActual = mutatingDto.dataString.get(addKey)
          assertResult(typeOfCase2_10)(caseNameActual)
          assertResult(testDto)(mutatingDto.copy(dataString = mutatingDto.dataString - addKey, process_timestamp = testDto.process_timestamp))
        }.getOrElse(assertResult(testDto)(mutatingDto.copy(dataString = mutatingDto.dataString, process_timestamp = testDto.process_timestamp)))
      }
    }
    assertTry(triedAssertions)
  }

  "Way4Cases " + testcasePostfix should " be some times error, with field " + addKey + " check all BusinessRulesService" in {

    val businessRulesService = BusinessRulesService(List("way4-case-2_2.drl", "way4-case-2_3.drl", "way4-case-2_4.drl", "way4-case-2_10.drl", "way4-case-5_2.drl", "way4-case-5_3.drl", "way4-case-11_2.drl"))
    val triedAssertions = testData()
      .map { test =>
        val dtoWithClassification = validUaspDto2_10.copy(dataString = validUaspDto2_10.dataString + (addKey -> "someValue"))
        val testDto = modifyListTestData(dtoWithClassification, test)
        val triedBoolean = Try {
          val mutatingDto = businessRulesService.map(testDto)

          test.head.expecped.map { q =>
            val dto = testDto.copy(dataString = testDto.dataString + (addKey -> (someValue + "," + typeOfCase2_10.value)))
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

object Way4Cases2_10Test {
  val validUaspDto2_10: UaspDto = UaspDto(
    "validUaspId",
    Map(),
    Map(
      "effective_date" -> 12344
    ),
    Map(),
    Map(),
    Map("transaction_amount" -> BigDecimal(152)),
    Map(
      "operation_id" -> "some operation_id ",
      "local_id" -> "way4Id",
      "processing_resolution" -> "asdsadadad",

      "action_type" -> "Authorization",

      "payment_direction" -> "Debit",
      "processing_result_code" -> "1",
    ),
    Map(),
    "validUaspUUID",
    13
  )

  def testcasePostfix = "2-10"

  def caseNameInMap = s"way4-case-$testcasePostfix"

  private def typeOfCase2_10: Some[String] = Some(caseNameInMap)

  protected def testData(): List[List[TestCaseData]] = List[List[TestCaseData]](
    List(TestString("action_type", NoneTestAction(), typeOfCase2_10)),

    List(TestString("operation_id", DeleteTestAction())),

    List(TestString("local_id", DeleteTestAction())),

    List(TestBigDecimal("transaction_amount", DeleteTestAction())),

    List(TestLong("effective_date", DeleteTestAction())),

    List(TestString("processing_resolution", DeleteTestAction())),

    List(TestString("action_type", DeleteTestAction())),
    List(TestString("action_type", AddTestAction("qwer"))),

    List(TestString("payment_direction", DeleteTestAction())),
    List(TestString("payment_direction", AddTestAction("qwer"))),

    List(TestString("processing_result_code", DeleteTestAction())),
    List(TestString("processing_result_code", AddTestAction("100"), typeOfCase2_10)),
    List(TestString("processing_result_code", AddTestAction("0"))),
    List(TestString("processing_result_code", AddTestAction("-1"))),
    List(TestString("processing_result_code", AddTestAction("-100"))),

  )

}







