package ru.vtb.uasp.mutator.way4

import io.qameta.allure.Feature
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.mutator.ValidUaspDtoGenerator.createValidUaspDto
import ru.vtb.uasp.mutator.service.BusinessRulesService.errFieldName
import ru.vtb.uasp.mutator.service.dto.{ConcatenateStr, StringMap, UaspOperation}
import ru.vtb.uasp.mutator.way4.ConstTest.{addKey, drlFileListConst, someValue}
import ru.vtb.uasp.mutator.way4.Way4Cases2_7Test._
import ru.vtb.uasp.mutator.way4.abstraction._

import scala.util.Try


@Feature("Way4Cases 2.7 Test")
class Way4Cases2_7Test extends AbstractDroolsTestCase {

  override protected def drlFileList: List[String] = drlFileListConst

  "Way4Cases " + testcasePostfix should " be ok, no field " + addKey in {

    val triedAssertions = testData().map { test =>
      test -> Try {
        val result = runDrools(validUaspDto2_7, test).toList
        test.head.expecped.map { q =>
          assertResult(1)(result.size)
          assertResult(result.head)(UaspOperation("2.7", StringMap(q), addKey, ConcatenateStr(",")))
        }.getOrElse(assertResult(0)(result.size))
      }
    }
    assertTry(triedAssertions)
  }

  "Way4Cases " + testcasePostfix should " be ok, no field " + addKey + " check all BusinessRulesService" in {

    val triedAssertions = testData().map { test =>
      test -> Try {
        val testDto = modifyListTestData(validUaspDto2_7, test)
        val mutatingDto = businessRulesService.processWithDlq(testDto)
        val rightUasp = mutatingDto.right.get
        test.head.expecped.map { q =>
          val caseNameActual = rightUasp.dataString.get(addKey)
          assertResult(typeOfCase2_7)(caseNameActual)
          assertResult(testDto)(rightUasp.copy(dataString = rightUasp.dataString - addKey, process_timestamp = testDto.process_timestamp))
        }.getOrElse(assertResult(testDto)(rightUasp.copy(dataString = rightUasp.dataString, process_timestamp = testDto.process_timestamp)))
      }
    }
    assertTry(triedAssertions)
  }

  "Way4Cases " + testcasePostfix should " be some times error, with field " + addKey + " check all BusinessRulesService" in {

    val triedAssertions = testData()
      .map { test =>
        val dtoWithClassification = validUaspDto2_7.copy(dataString = validUaspDto2_7.dataString + (addKey -> "someValue"))
        val testDto = modifyListTestData(dtoWithClassification, test)
        val triedBoolean = Try {
          val mutatingDto = businessRulesService.processWithDlq(testDto)
          val rightUasp = mutatingDto.right.get

          test.head.expecped.map { q =>
            val dto = testDto.copy(dataString = testDto.dataString + (addKey -> (someValue + "," + typeOfCase2_7.value)))
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

object Way4Cases2_7Test {
  def testcasePostfix = "2-7"

  val validUaspDto2_7: UaspDto = createValidUaspDto("1", testcasePostfix)

  def caseNameInMap = s"way4-case-$testcasePostfix"

  private def typeOfCase2_7: Some[String] = Some(caseNameInMap)

  protected def testData(): List[List[TestCaseData]] = List[List[TestCaseData]](

    List(TestString("action_type", DeleteTestAction())),
    List(TestString("service_type", DeleteTestAction())),
    List(TestLong("service_datetime", DeleteTestAction())),
    List(TestLong("effective_date", DeleteTestAction())),
    List(TestString("processing_resolution", DeleteTestAction())),
    List(TestBigDecimal("fee_amount", DeleteTestAction())),
    //
    List(TestString("action_type", AddTestAction("!Presentment"))),
    List(TestString("service_type", AddTestAction("!F7"))),
    List(TestString("processing_resolution", AddTestAction("!Accepted"))),
    //    List(TestBigDecimal("fee_amount", AddTestAction(BigDecimal.valueOf(2).toString())))

  )

}







