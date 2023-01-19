
package ru.vtb.uasp.vector.cases

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.libs.json.{JsObject, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.vector.dsl.RuleParser
import ru.vtb.uasp.vector.service.RuleApplyMapFunction
import ru.vtb.uasp.vector.util.TestUtil.testHarnessCreator


class RuleCase71Test extends AnyFlatSpec {
  private val packageServiceProcessFunction = new RuleApplyMapFunction

  behavior of "RuleCase71Test"


  private val uasp = UaspDto(
    "1234ID",
    Map("currency_scale" -> 0, "period" -> 121355),
    Map("event_dttm" -> 1667660331000L),
    Map.empty,
    Map.empty,
    Map("currency_price" -> BigDecimal(1), "amount" -> BigDecimal(150), "product_rate" -> BigDecimal(150), "cashbackTotal" -> BigDecimal(4), "transaction_amount_prf" -> BigDecimal(21)),
    Map(
      "account_type" -> "AccountTestType",
      "source_system_prf" -> "Profile",
      "system-classification" -> "RTO_71_case",
      "OPERATION_ID_Case_71" -> "op_id",
      "OPERATION_NM" -> "op nm",
      "PRODUCT_TYPE" -> "PRODUCT_TYPE",
      "exchange_currency" -> "RUR",
      "DEP_ACCOUNT" -> "123",
      "source_account" -> "12332414",
      "product_nm" -> "productNHm",
      "payment_system_desc" -> "Visa",
      "terminal_owner" -> "Mira",
      "categoryName" -> "Магазин"
    ),
    Map.empty,
    "",
    0L)

  it should "Case 71 should be ok" in {
    val testHarness = testHarnessCreator(packageServiceProcessFunction, uasp)

    val packageServiceDlq = testHarness.getSideOutput(RuleApplyMapFunction.dlqRuleApplyMapFunction)
    packageServiceDlq shouldBe null

    val packageServiceOutDto = testHarness.getSideOutput(RuleParser.tags("RTO_71_case"))
    val result = Json.parse(new String(packageServiceOutDto.peek().getValue.value)).as[JsObject]
    result.-("KAFKA_DTTM").toString() shouldBe
      """{
        |"MDM_ID":"1234ID",
        |"OPERATION_ID":"op_id",
        |"EVENT_DTTM":"2022-11-05 17:58:51",
        |"PRODUCT_TYPE":"PRODUCT_TYPE",
        |"PRODUCT_NM":"productNHm",
        |"PRODUCT_RATE":150,
        |"OPERATION_NM":"op nm",
        |"RUR_AMOUNT":150,
        |"AMOUNT":21,
        |"CURRENCY_ISO":"RUR",
        |"PERIOD":121355,
        |"DEP_ACCOUNT":"123******",
        |"MC_ACCOUNT":"12332414******"
        |}"""
        .stripMargin
        .replaceAll("\n", "")
        .replaceAll("\r", "")
  }

}
