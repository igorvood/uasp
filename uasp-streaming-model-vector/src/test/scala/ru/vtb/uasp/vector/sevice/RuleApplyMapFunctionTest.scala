package ru.vtb.uasp.vector.sevice

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.libs.json.{JsObject, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.vector.dsl.RuleParser
import ru.vtb.uasp.vector.service.RuleApplyMapFunction
import ru.vtb.uasp.vector.util.TestUtil.testHarnessCreator


class RuleApplyMapFunctionTest extends AnyFlatSpec {
  private val packageServiceProcessFunction = new RuleApplyMapFunction

  behavior of "RuleApplyMapFunctionTest"

  private val uasp = UaspDto(
    "1234ID",
    Map.empty,
    Map("event_dttm" -> 1667660331000L),
    Map.empty,
    Map.empty,
    Map("transaction_amount" -> BigDecimal(234), "amount" -> BigDecimal(150), "BALANCE_RUR" -> BigDecimal(234), "endbal_prf" -> BigDecimal(2134), "amount" -> BigDecimal(150)),
    Map(
      "source_system_prf" -> "Profile",
      "system-classification" -> "RTO_29_case,RTO_8_case",
      "OPERATION_ID_Case_29" -> "op_id",
      "OPERATION_INFO" -> "edfrghjk",
      "account_dep" -> "437276438756238742565",
      "exchange_currency" -> "RUR",
      "kbo" -> "53276",
      "account_name" -> "25424524625",
      "OPERATION_STATUS" -> "Sale",
      "source_account" -> "1234567891012141516",
      "exchange_currency" -> "RUR",
      "OPERATION_ID" -> "op_id",
      "exchange_currency" -> "RUR",
      "ACCOUNT_NM" -> "Some account nm",
      "OPERATION_STATUS" -> "Sale",
      "source_account" -> "1234567891012141516"
    ),
    Map.empty,
    "",
    0L)

  it should "Case 29 and 8 should be ok" in {
    val testHarness = testHarnessCreator(packageServiceProcessFunction, uasp)

    val packageServiceDlq = testHarness.getSideOutput(RuleApplyMapFunction.dlqRuleApplyMapFunction)
    packageServiceDlq shouldBe null

    val packageServiceOutDtoCase29 = testHarness.getSideOutput(RuleParser.tags("RTO_29_case"))
    val packageServiceOutDtoCase8 = testHarness.getSideOutput(RuleParser.tags("RTO_8_case"))

    val result29 = Json.parse(new String(packageServiceOutDtoCase29.peek().getValue.value)).as[JsObject]
    result29.-("KAFKA_DTTM").toString() shouldBe
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

    val result8 = Json.parse(new String(packageServiceOutDtoCase8.peek().getValue.value)).as[JsObject]
    result8.-("KAFKA_DTTM").toString() shouldBe
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

  it should "Case 29 and 8 should be one failed and one ok " in {
    val testHarness = testHarnessCreator(packageServiceProcessFunction, uasp.copy(dataString = uasp.dataString.filter(_._1 != "OPERATION_ID_Case_29")))

    val packageServiceDlq = testHarness.getSideOutput(RuleApplyMapFunction.dlqRuleApplyMapFunction)
    packageServiceDlq.size shouldBe 1


    val packageServiceOutDtoCase29 = testHarness.getSideOutput(RuleParser.tags("RTO_29_case"))
    packageServiceOutDtoCase29 shouldBe null

    val packageServiceOutDtoCase8 = testHarness.getSideOutput(RuleParser.tags("RTO_8_case"))

    val result8 = Json.parse(new String(packageServiceOutDtoCase8.peek().getValue.value)).as[JsObject]
    result8.-("KAFKA_DTTM").toString() shouldBe
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
