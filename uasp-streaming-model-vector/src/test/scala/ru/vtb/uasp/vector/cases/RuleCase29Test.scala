package ru.vtb.uasp.vector.cases

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.libs.json.{JsObject, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.vector.dsl.RuleParser
import ru.vtb.uasp.vector.service.RuleApplyMapFunction
import ru.vtb.uasp.vector.util.TestUtil.testHarnessCreator


class RuleCase29Test extends AnyFlatSpec {
  private val packageServiceProcessFunction = new RuleApplyMapFunction

  behavior of "RuleCase29Test"

  private val uasp = UaspDto(
    "1234ID",
    Map.empty,
    Map("event_dttm" -> 1667660331000L),
    Map.empty,
    Map.empty,
    Map("transaction_amount" -> BigDecimal(234), "amount" -> BigDecimal(150)),
    Map(
      "source_system_prf" -> "Profile",
      "system-classification" -> "RTO_29_case",
      "OPERATION_ID_Case_29" -> "op_id",
      "OPERATION_INFO" -> "edfrghjk",
      "exchange_currency" -> "RUR",
      "kbo" -> "53276",
      "account_name" -> "25424524625",
      "OPERATION_STATUS" -> "Sale",
      "source_account" -> "1234567891012141516",
      "exchange_currency" -> "RUR"
    ),
    Map.empty,
    "",
    0L)

  it should "Case 29 should be ok" in {
    val testHarness = testHarnessCreator(packageServiceProcessFunction, uasp)

    val packageServiceDlq = testHarness.getSideOutput(RuleApplyMapFunction.dlqRuleApplyMapFunction)
    packageServiceDlq shouldBe null

    val packageServiceOutDto = testHarness.getSideOutput(RuleParser.tags("RTO_29_case"))
    val result = Json.parse(new String(packageServiceOutDto.peek().getValue.value)).as[JsObject]
    result.-("KAFKA_DTTM").toString() shouldBe
    """{
        |"MDM_ID":"1234ID",
        |"OPERATION_ID":"op_id",
        |"EVENT_DTTM":"2022-11-05 17:58:51",
        |"ACCOUNT_NUMBER":"1234567891******516",
        |"OPERATION_CODE":"53276",
        |"OPERATION_INFO":"edfrghjk",
        |"OPERATION_RUR_AMOUNT":150,
        |"CURRENCY_NM":"RUR",
        |"OPERATION_STATUS":"Sale"
        |}"""
      .stripMargin
      .replaceAll("\n", "")
      .replaceAll("\r", "")
  }

}
