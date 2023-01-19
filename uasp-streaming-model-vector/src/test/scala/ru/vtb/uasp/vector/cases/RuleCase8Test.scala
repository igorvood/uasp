package ru.vtb.uasp.vector.cases

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.libs.json.{JsObject, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.vector.dsl.RuleParser
import ru.vtb.uasp.vector.service.RuleApplyMapFunction
import ru.vtb.uasp.vector.util.TestUtil.testHarnessCreator


class RuleCase8Test extends AnyFlatSpec {
  private val packageServiceProcessFunction = new RuleApplyMapFunction

  behavior of "RuleCase8Test"

  private val uasp = UaspDto(
    "1234ID",
    Map.empty,
    Map("event_dttm" -> 1667660331000L),
    Map.empty,
    Map.empty,
    Map("BALANCE_RUR" -> BigDecimal(234), "endbal_prf" -> BigDecimal(2134), "amount" -> BigDecimal(150)),
    Map(
      "source_system_prf" -> "Profile",
      "system-classification" -> "RTO_8_case",
      "OPERATION_ID" -> "op_id",
      "exchange_currency" -> "RUR",
      "ACCOUNT_NM" -> "Some account nm",
      "OPERATION_STATUS" -> "Sale",
      "source_account" -> "1234567891012141516"
    ),
    Map.empty,
    "",
    0L)

  it should "Case 8 should be ok" in {
    val testHarness = testHarnessCreator(packageServiceProcessFunction, uasp)

    val packageServiceDlq = testHarness.getSideOutput(RuleApplyMapFunction.dlqRuleApplyMapFunction)
    packageServiceDlq shouldBe null

    val packageServiceOutDto = testHarness.getSideOutput(RuleParser.tags("RTO_8_case"))
    val result = Json.parse(new String(packageServiceOutDto.peek().getValue.value)).as[JsObject]
    result.-("KAFKA_DTTM").toString() shouldBe
      """{
        |"MDM_ID":"1234ID",
        |"OPERATION_ID":"op_id",
        |"EVENT_DTTM":"2022-11-05 17:58:51",
        |"ACCOUNT_NUMBER":"1234567891******516",
        |"ACCOUNT_NM":"Some account nm",
        |"OPERATION_RUR_AMOUNT":150,
        |"BALANCE":2134,
        |"BALANCE_RUR":234,
        |"CURRENCY_ISO":"RUR",
        |"OPERATION_STATUS":"Sale"
        |}"""
        .stripMargin
        .replaceAll("\n", "")
        .replaceAll("\r", "")
  }

}
