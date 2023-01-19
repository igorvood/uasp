package ru.vtb.uasp.vector.cases

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.libs.json.{JsObject, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.vector.dsl.RuleParser
import ru.vtb.uasp.vector.service.RuleApplyMapFunction
import ru.vtb.uasp.vector.util.TestUtil.testHarnessCreator


class RuleCase38Test extends AnyFlatSpec {
  private val packageServiceProcessFunction = new RuleApplyMapFunction

  behavior of "RuleCase38Test"

  private val uasp = UaspDto(
    "1234ID",
    Map("EVENT_CNT" -> 347689),
    Map("event_dttm" -> 1667660331000L),
    Map.empty,
    Map.empty,
    Map("amount_sum_cft" -> BigDecimal(234), "amount" -> BigDecimal(150)),
    Map(
      "source_system_cft" -> "CFT2RS",
      "system-classification" -> "RTO_First_NS_38_case",
      "operation_id_cft" -> "op_id",
      "account_dep_cft" -> "437276438756238742565",
      "account_name_cft" -> "25424524625",
      "exchange_currency" -> "RUR"
    ),
    Map.empty,
    "",
    0L)

  it should "Case 38 should be ok" in {
    val testHarness = testHarnessCreator(packageServiceProcessFunction, uasp)

    val packageServiceDlq = testHarness.getSideOutput(RuleApplyMapFunction.dlqRuleApplyMapFunction)
    packageServiceDlq shouldBe null

    val packageServiceOutDto = testHarness.getSideOutput(RuleParser.tags("RTO_First_NS_38_case"))
    val result = Json.parse(new String(packageServiceOutDto.peek().getValue.value)).as[JsObject]
    result.-("KAFKA_DTTM").toString() shouldBe
    """{
        |"MDM_ID":"1234ID",
        |"OPERATION_ID":"op_id",
        |"EVENT_DTTM":"2022-11-05 17:58:51",
        |"EVENT_CNT":347689,
        |"PRODUCT_TYPE_CD":"437276438756238742565",
        |"ACCOUNT_NAME":"25424524625",
        |"OPERATION_AMT":234,
        |"ACCOUNT_CURRENCY":"RUR",
        |"OPERATION_RUR_AMT":150
        |}"""
      .stripMargin
      .replaceAll("\n", "")
      .replaceAll("\r", "")
  }

}
