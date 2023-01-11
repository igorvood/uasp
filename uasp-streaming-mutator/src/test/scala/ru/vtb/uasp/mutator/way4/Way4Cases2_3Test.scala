package ru.vtb.uasp.mutator.way4

import io.qameta.allure.Feature
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.mutator.service.BusinessRulesService.errFieldName
import ru.vtb.uasp.mutator.service.dto.{ConcatenateStr, StringMap, UaspOperation}
import ru.vtb.uasp.mutator.way4.ConstTest.{addKey, drlFileListConst, someValue}
import ru.vtb.uasp.mutator.way4.Way4Cases2_3Test._
import ru.vtb.uasp.mutator.way4.abstraction._

import scala.util.Try


@Feature("Way4Cases 2.3 Test")
class Way4Cases2_3Test extends AbstractDroolsTestCase {

  override protected def drlFileList: List[String] = drlFileListConst

  "Way4Cases " + testcasePostfix should " be ok, no field " + addKey in {

    val triedAssertions = testData().map { test =>
      test -> Try {
        val result = runDrools(validUaspDto2_3, test).toList
        test.head.expecped.map { q =>
          assertResult(1)(result.size)
          assertResult(result.head)(UaspOperation("2.3", StringMap(q), addKey, ConcatenateStr(",")))
        }.getOrElse(assertResult(0)(result.size))
      }
    }
    assertTry(triedAssertions)
  }

  "Way4Cases " + testcasePostfix should " be ok, no field " + addKey + " check all BusinessRulesService" in {

    val triedAssertions = testData().map { test =>
      test -> Try {
        val testDto = modifyListTestData(validUaspDto2_3, test)
        val mutatingDto = businessRulesService.processWithDlq(testDto)
        val rightUasp = mutatingDto.right.get

        test.head.expecped.map { q =>
          val caseNameActual = rightUasp.dataString.get(addKey)
          assertResult(typeOfCase2_3)(caseNameActual)
          assertResult(testDto)(rightUasp.copy(dataString = rightUasp.dataString - addKey, process_timestamp = testDto.process_timestamp))
        }.getOrElse(assertResult(testDto)(rightUasp.copy(dataString = rightUasp.dataString, process_timestamp = testDto.process_timestamp)))
      }
    }
    assertTry(triedAssertions)
  }

  "Way4Cases " + testcasePostfix should " be some times error, with field " + addKey + " check all BusinessRulesService" in {

    val triedAssertions = testData()
      .map { test =>
        val dtoWithClassification = validUaspDto2_3.copy(dataString = validUaspDto2_3.dataString + (addKey -> "someValue"))
        val testDto = modifyListTestData(dtoWithClassification, test)
        val triedBoolean = Try {
          val mutatingDto = businessRulesService.processWithDlq(testDto)
          val rightUasp = mutatingDto.right.get
          test.head.expecped.map { q =>
            val dto = testDto.copy(dataString = testDto.dataString + (addKey -> (someValue + "," + typeOfCase2_3.value)))
            assertResult(dto)(rightUasp.copy(dataString = rightUasp.dataString - errFieldName, process_timestamp = dto.process_timestamp))
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

object Way4Cases2_3Test {
  val validUaspDto2_3: UaspDto = UaspDto(
    "validUaspId",
    Map(),
    Map(
      "transaction_datetime" -> 1512,
      "effective_date" -> 12344
    ),
    Map(),
    Map(),
    Map("transaction_amount" -> BigDecimal(152)),
    Map(
      "operation_id" -> "some operation_id ",
      "local_id" -> "way4Id",
      "audit_srn" -> "audit_srn",
      "audit_auth_code" -> "audit_auth_code",
      "audit_rrn" -> "audit_rrn",

      "action_type" -> "AuthorizationReversal",
      "processing_resolution" -> "Accepted",
    ),
    Map(),
    "validUaspUUID",
    13
  )

  def testcasePostfix = "2-3"

  def caseNameInMap = s"way4-case-$testcasePostfix"

  private def typeOfCase2_3: Some[String] = Some(caseNameInMap)

  protected def testData(): List[List[TestCaseData]] = List[List[TestCaseData]](
    //    тестирование с истинной в первой части "Or"
    List(TestString("action_type", NoneTestAction(), typeOfCase2_3)),

    List(TestString("operation_id", DeleteTestAction())),

    List(TestString("local_id", DeleteTestAction())),

    List(TestString("audit_srn", DeleteTestAction())),

    List(TestLong("transaction_datetime", DeleteTestAction())),

    List(TestLong("effective_date", DeleteTestAction())),

    List(TestString("audit_auth_code", DeleteTestAction())),

    List(TestString("audit_rrn", DeleteTestAction())),

    List(TestString("action_type", DeleteTestAction())),
    List(TestString("action_type", AddTestAction("qwer"))),

    List(TestString("processing_resolution", DeleteTestAction())),
    List(TestString("processing_resolution", AddTestAction("qwer"))),

    //    тестирование с истинной во второй части "Or"
    List(TestString("action_type", AddTestAction("PresentmentReversal"), typeOfCase2_3)),

    //    тестирование с истинной во второй части "Or"
    List(TestString("action_type", AddTestAction("Presentment"), typeOfCase2_3),
      TestString("processing_resolution", AddTestAction("Rejected")),
    ),
  )

}




