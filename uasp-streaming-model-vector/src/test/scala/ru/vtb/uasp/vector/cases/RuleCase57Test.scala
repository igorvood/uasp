
package ru.vtb.uasp.vector.cases

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.libs.json.{JsObject, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.vector.dsl.RuleParser
import ru.vtb.uasp.vector.service.RuleApplyMapFunction
import ru.vtb.uasp.vector.util.TestUtil.testHarnessCreator


class RuleCase57Test extends AnyFlatSpec {
  private val packageServiceProcessFunction = new RuleApplyMapFunction

  behavior of "RuleCase57Test"

  private val uasp = UaspDto(
    "1234ID",
    Map("EVENT_CNT" -> 347689),
    Map("event_dttm" -> 1667660331000L),
    Map.empty,
    Map.empty,
    Map("amount" -> BigDecimal(150)),
    Map(
      "account_type_prf" -> "AccountTestType",
      "source_system_prf" -> "Profile",
      "mdm_id" -> "1234ID",
      "system-classification" -> "RTO_First_pension_57_case",
      "OPERATION_ID" -> "op_id",
      "SOURCE_SYSTEM" -> "afadg",
      "source_acc" -> "2363635623636356",
      "kbo" -> "53276",
      "source_account" -> "1234567891012141516"
    ),
    Map.empty,
    "",
    0L)

  it should "Case 57 should be ok" in {
    val testHarness = testHarnessCreator(packageServiceProcessFunction, uasp)

    val packageServiceDlq = testHarness.getSideOutput(RuleApplyMapFunction.dlqRuleApplyMapFunction)
    packageServiceDlq shouldBe null

    val packageServiceOutDto = testHarness.getSideOutput(RuleParser.tags("RTO_First_pension_57_case"))
    val result = Json.parse(new String(packageServiceOutDto.peek().getValue.value)).as[JsObject]
    result.-("KAFKA_DTTM").toString() shouldBe
    """{
        |"MDM_ID":"1234ID",
        |"OPERATION_ID":"op_id",
        |"EVENT_DTTM":"2022-11-05 17:58:51",
        |"TRANS_AMOUNT":150,
        |"KBO":"53276",
        |"EVENT_CNT":347689,
        |"SOURCE_ACCOUNT":"1234567891******516",
        |"SOURCE_SYSTEM":"afadg",
        |"ACCOUNT_TYPE":"AccountTestType"
        |}"""
      .stripMargin
      .replaceAll("\n", "")
      .replaceAll("\r", "")
  }

}
