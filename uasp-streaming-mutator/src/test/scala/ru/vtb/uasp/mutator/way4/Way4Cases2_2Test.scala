package ru.vtb.uasp.mutator.way4

import io.qameta.allure.Feature
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.mutator.ValidUaspDtoGenerator.serviceDataDto
import ru.vtb.uasp.mutator.service.BusinessRulesService
import ru.vtb.uasp.mutator.service.BusinessRulesService.errFieldName
import ru.vtb.uasp.mutator.service.dto.{ConcatenateStr, StringMap, UaspOperation}
import ru.vtb.uasp.mutator.way4.ConstTest.{addKey, drlFileListConst, someValue}
import ru.vtb.uasp.mutator.way4.Way4Cases2_2Test.{testData, typeOfCase2_2, validUaspDto2_2}
import ru.vtb.uasp.mutator.way4.abstraction._

import scala.util.Try


@Feature("Way4Cases 2.2 Test")
class Way4Cases2_2Test extends AbstractDroolsTestCase {

  override protected def drlFileList: List[String] = drlFileListConst

  private val testcasePostfix = "2.2"
  "Way4Cases " + testcasePostfix should " be ok, no field " + addKey in {

    val triedAssertions = testData().map { test =>
      test -> Try {
        val result = runDrools(validUaspDto2_2, test).toList
        test.expecped.map { q =>
          assertResult(1)(result.size)
          assertResult(result.head)(UaspOperation(testcasePostfix, StringMap(q), addKey, ConcatenateStr(",")))
        }.getOrElse(assertResult(0)(result.size))
      }
    }
    assertTry(triedAssertions)
  }

  "Way4Cases " + testcasePostfix should " be ok, no field " + addKey + " check all BusinessRulesService" in {

    val triedAssertions = testData().map { test =>
      test -> Try {
        val testDto = modifyTestData(validUaspDto2_2, test)
        val mutatingDto = businessRulesService.processWithDlq(testDto)
        val rightUasp = mutatingDto.right.get
        test.expecped.map { q =>

          val caseNameActual = rightUasp.dataString.get(addKey)
          assertResult(typeOfCase2_2)(caseNameActual)
          assertResult(testDto)(rightUasp.copy(dataString = rightUasp.dataString - addKey, process_timestamp = testDto.process_timestamp))
        }.getOrElse {
          assertResult(testDto)(rightUasp.copy(dataString = rightUasp.dataString, process_timestamp = testDto.process_timestamp))
        }
      }
    }
    assertTry(triedAssertions)
  }

  "Way4Cases " + testcasePostfix should " be some times error, with field " + addKey + " check all BusinessRulesService" in {

    val businessRulesService = BusinessRulesService(serviceDataDto, List("way4-case-2_2.drl", "way4-case-2_3.drl", "way4-case-2_4.drl", "way4-case-2_10.drl", "way4-case-5_2.drl", "way4-case-5_3.drl", "way4-case-11_2.drl"))
    val triedAssertions = testData()
      .map { test =>
        val dtoWithClassification = validUaspDto2_2.copy(dataString = validUaspDto2_2.dataString + (addKey -> "someValue"))
        val testDto = modifyTestData(dtoWithClassification, test)
        val triedBoolean = Try {
          val mutatingDto = businessRulesService.processWithDlq(testDto)
          val rightUasp = mutatingDto.right.get
          test.expecped.map { q =>
            val dto = testDto.copy(dataString = testDto.dataString + (addKey -> (someValue + "," + typeOfCase2_2.value)))
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

object Way4Cases2_2Test {
  val validUaspDto2_2: UaspDto = UaspDto(
    "validUaspId",
    Map(),
    Map("transaction_datetime" -> 1512,
      "effective_date" -> 12344
    ),
    Map(),
    Map(),
    Map("transaction_amount" -> BigDecimal(152)),
    Map(
      "local_id" -> "way4Id",
      "action_type" -> "Presentment",
      "audit_auth_code" -> "some audit_auth_code",
      "audit_rrn" -> "audit_rrn",
      "audit_srn" -> "audit_srn",
      "processing_resolution" -> "Accepted",
      "payment_direction" -> "Debit",
    ),
    Map(),
    "validUaspUUID",
    13
  )

  def caseNameInMap = "way4-case-2-2"

  private def typeOfCase2_2: Some[String] = Some(caseNameInMap)

  protected def testData(): List[TestCaseData] = List[TestCaseData](
    TestString("action_type", NoneTestAction(), typeOfCase2_2),
    TestString("local_id", DeleteTestAction()),

    TestString("action_type", DeleteTestAction()),
    TestString("action_type", AddTestAction("qwer")),

    TestString("audit_auth_code", DeleteTestAction()),

    TestString("audit_rrn", DeleteTestAction()),

    TestString("audit_srn", DeleteTestAction()),

    TestBigDecimal("transaction_amount", DeleteTestAction()),

    TestLong("transaction_datetime", DeleteTestAction()),

    TestLong("effective_date", DeleteTestAction()),

    TestString("processing_resolution", DeleteTestAction()),
    TestString("processing_resolution", AddTestAction("qwer")),

    TestString("payment_direction", DeleteTestAction()),
    TestString("payment_direction", AddTestAction("qwer")),


  )

}



