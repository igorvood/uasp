package ru.vtb.uasp.mutator.way4

import io.qameta.allure.Feature
import io.qameta.allure.scalatest.AllureScalatestContext
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.extension.CommonExtension.Also
import ru.vtb.uasp.mutator.configuration.drools.KieBaseService
import ru.vtb.uasp.mutator.service.drools.DroolsRunner
import ru.vtb.uasp.mutator.service.dto.{Add, ConcatenateStr, StringMap, UaspOperation}
import ru.vtb.uasp.mutator.way4.ConstTest.addKey

@Feature("Way4CasesTest")
class Way4CasesTest extends AnyFlatSpec with should.Matchers {

  val uaspDto: UaspDto = DataGenerateUtil.getWay4UaspDto

  val validator: DroolsRunner = new KieBaseService(List("way4-case-2_2.drl", "way4-case-2_3.drl",
    "way4-case-2_4.drl", "way4-case-2_10.drl", "way4-case-5_2.drl", "way4-case-5_3.drl", "way4-case-11_2.drl"))
    .also { q => DroolsRunner(q.kBase) }

  def runDrools(uaspDto: UaspDto): Set[UaspOperation] =
    validator.apply(uaspDto, { case x: UaspOperation => x })

  "The invalid UaspDto message" should "not validate cases 2.2, 2.3, 2.4, 2.10" in new AllureScalatestContext {

    val invalidUaspDto: UaspDto = uaspDto.copy()
    println("uaspDto: " + invalidUaspDto)
    val result: Set[UaspOperation] = runDrools(invalidUaspDto)
    println("result: " + result)
    result shouldBe empty
  }

  "The valid UaspDto message" should "validate case 2.2" in new AllureScalatestContext {

    val validUaspDto: UaspDto = uaspDto.copy(id = "2157291480",
      dataInt = uaspDto.dataInt + ("processing_result_code" -> 0),
      dataLong = uaspDto.dataLong
        + ("transaction_datetime" -> 1626621144000L)
        + ("processing_datetime" -> 1626631944000L)
        + ("effective_date" -> 1626555600000L),
      dataDecimal = uaspDto.dataDecimal + ("transaction_amount" -> BigDecimal(6651.78)),
      dataString = uaspDto.dataString
        + ("action_type" -> "Presentment")
        + ("audit_auth_code" -> "3687")
        + ("processing_resolution" -> "Accepted")
        + ("action_type" -> "Presentment")
        + ("payment_direction" -> "Debit"),
    )
    println("uaspDto: " + validUaspDto)
    val result: Set[UaspOperation] = runDrools(validUaspDto)
    println("result: " + result)

    result.toList should (
      (have length 1)
        and
        contain(UaspOperation("2.2", StringMap("way4-case-2-2"), addKey, ConcatenateStr(",")))
      )
  }

  "The valid UaspDto message" should "validate case 2.3" in new AllureScalatestContext {

    val validUaspDto: UaspDto = uaspDto.copy(id = "2157291480",
      dataLong = uaspDto.dataLong
        + ("transaction_datetime" -> 1626621144000L)
        + ("effective_date" -> 1626555600000L),
      dataString = uaspDto.dataString
        + ("action_type" -> "AuthorizationReversal")
        + ("audit_auth_code" -> "3687")
        + ("operation_id" -> "493900")
        + ("processing_resolution" -> "Accepted")
    )
    println("uaspDto: " + validUaspDto)
    val result: Set[UaspOperation] = runDrools(validUaspDto)
    println("result: " + result)
    result.toList should ((have length 1) and
      contain(UaspOperation("2.3", StringMap("way4-case-2-3"), addKey, ConcatenateStr(",")))
      )
  }

  "The valid UaspDto message" should "validate case 2.4" in new AllureScalatestContext {

    val validUaspDto: UaspDto = uaspDto.copy(
      dataLong = uaspDto.dataLong
        + ("effective_date" -> 1626555600000L),
      dataString = uaspDto.dataString
        + ("operation_id" -> "493900")
        + ("action_type" -> "Presentment")
        + ("audit_auth_code" -> "")
        + ("audit_rrn" -> "")
        + ("payment_direction" -> "Debit")
        + ("processing_resolution" -> "Accepted")
    )
    println("uaspDto: " + validUaspDto)
    val result: Set[UaspOperation] = runDrools(validUaspDto)
    println("result: " + result)
    result.toList should ((have length 1) and
      contain(UaspOperation("2.4", StringMap("way4-case-2-4"), addKey, ConcatenateStr(",")))
      )
  }

  "The valid UaspDto message" should "validate case 2.10" in new AllureScalatestContext {

    val validUaspDto: UaspDto = uaspDto.copy(
      dataDecimal = uaspDto.dataDecimal
        + ("transaction_amount" -> BigDecimal(6651.78)),
      dataLong = uaspDto.dataLong
        + ("transaction_datetime" -> 1626621144000L)
        + ("effective_date" -> 1626555600000L),
      dataString = uaspDto.dataString
        + ("processing_result_code" -> "1")
        + ("operation_id" -> "493900")
        + ("action_type" -> "Authorization")
        + ("processing_resolution" -> "Accepted")
        + ("action_type" -> "Authorization")
        + ("payment_direction" -> "Debit")
    )
    println("uaspDto: " + validUaspDto)
    val result: Set[UaspOperation] = runDrools(validUaspDto)
    println("result: " + result)
    result.toList should ((have length 1) and
      contain(UaspOperation("2.10", StringMap("way4-case-2-10"), addKey, ConcatenateStr(",")))
      )
  }
}
