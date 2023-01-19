package ru.vtb.uasp.vector.cases

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.libs.json.{JsObject, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.vector.dsl.RuleParser
import ru.vtb.uasp.vector.service.RuleApplyMapFunction
import ru.vtb.uasp.vector.util.TestUtil.testHarnessCreator


class RuleCase39NewTest extends AnyFlatSpec {
  private val packageServiceProcessFunction = new RuleApplyMapFunction

  behavior of "RuleCase39NewTest"

  private val uasp = UaspDto(
    "1234ID",
    Map("currency_scale" -> 0, "EVENT_CNT" -> 347689),
    Map("event_dttm" -> 1667660331000L),
    Map.empty,
    Map.empty,
    Map("transaction_amt" -> BigDecimal(234), "endbal_prf" -> BigDecimal(2134), "amount" -> BigDecimal(150), "cashbackTotal" -> BigDecimal(4), "lostBenefit" -> BigDecimal(21)),
    Map(
      "source_system_prf" -> "Profile",
      "system-classification" -> "RTO_First_POS_39New_case",
      "msgid" -> "op_id",
      "card_type_cd" -> "asjjsdahb",
      "account_dep" -> "437276438756238742565",
      "card_number" -> "523436",
      "exchange_currency" -> "RUR",
      "package_nm" -> "CLASSIC",
      "response_code" -> "orc sam",
      "account_name" -> "25424524625",
      "POS_FLG" -> "Y",
      "FIRST_CLIENT_POS_FLG" -> "Y",
      "exchange_currency" -> "RUR",
      "is_virtual_card_flg" -> "N",
      "salary_card_type_flg" -> "N",
      "OPERATION_STATUS" -> "Sale",
      "CARD_TYPE" -> "DEBET"
    ),
    Map.empty,
    "",
    0L)

  it should "Case 39 new should be ok" in {
    val testHarness = testHarnessCreator(packageServiceProcessFunction, uasp)

    val packageServiceDlq = testHarness.getSideOutput(RuleApplyMapFunction.dlqRuleApplyMapFunction)
    packageServiceDlq shouldBe null

    val packageServiceOutDto = testHarness.getSideOutput(RuleParser.tags("RTO_First_POS_39New_case"))
    val result = Json.parse(new String(packageServiceOutDto.peek().getValue.value)).as[JsObject]
    result.-("KAFKA_DTTM").toString() shouldBe
    """{
        |"MDM_ID":"1234ID",
        |"OPERATION_ID":"op_id",
        |"EVENT_DTTM":"2022-11-05 17:58:51",
        |"CARD_NUMBER":"523436",
        |"AMOUNT":234,
        |"CURRENCY_ISO":"RUR",
        |"RUR_AMOUNT":150,
        |"STATUS_CD":"orc sam",
        |"POS_FLG":"Y",
        |"CARD_TYPE":"ASJJSDAHB",
        |"SALARY_FLG":"N",
        |"CARD_FORM":"Пластиковая",
        |"PACKAGE_NM":"CLASSIC",
        |"FIRST_CLIENT_POS_FLG":"Y"
        |}"""
      .stripMargin
      .replaceAll("\n", "")
      .replaceAll("\r", "")
  }

}
