package ru.vtb.uasp.vector.cases

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.libs.json.{JsObject, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.vector.dsl.RuleParser
import ru.vtb.uasp.vector.service.RuleApplyMapFunction
import ru.vtb.uasp.vector.util.TestUtil.testHarnessCreator


class RuleCase44Test extends AnyFlatSpec {
  private val packageServiceProcessFunction = new RuleApplyMapFunction

  behavior of "RuleCase44Test"

  private val uasp = UaspDto(
    "1234ID",
    Map.empty,
    Map("event_dttm" -> 1667660331000L, "DELAY_TIME" -> 1667660331000L),
    Map.empty,
    Map("PART_SUMM" -> 34.5),
    Map("AMOUNT_ALL" -> BigDecimal(234), "amount" -> BigDecimal(150)),
    Map(
      "system-classification" -> "RTO_44_case",
      "OPERATION_ID" -> "op_id",
      "transferOrderId" -> "sdaf32wq2",
      "receiverFpsBankId" -> "CLASSIC",
      "receiverName" -> "receiverNameSASD",
      "senderName" -> "asenderName",
      "interactionChannel" -> "TestinteractionChanneldas",
      "targetMaskedPan" -> "orc sam",
      "targetAccount" -> "1234567891012141516",
      "targetBankRussianName" -> "afadg",
      "sourceMaskedPan" -> "3636356",
      "sourceAccount" -> "1114567891012141555",
      "kbo" -> "53276"
    ),
    Map("is_withdraw" -> true),
    "",
    0L)

  it should "Case 44 should be ok" in {
    val testHarness = testHarnessCreator(packageServiceProcessFunction, uasp)

    val packageServiceDlq = testHarness.getSideOutput(RuleApplyMapFunction.dlqRuleApplyMapFunction)
    packageServiceDlq shouldBe null

    val packageServiceOutDto = testHarness.getSideOutput(RuleParser.tags("RTO_44_case"))
    val result = Json.parse(new String(packageServiceOutDto.peek().getValue.value)).as[JsObject]
    result.-("KAFKA_DTTM").toString() shouldBe
    """{
        |"MDM_ID":"1234ID",
        |"OPERATION_ID":"sdaf32wq2",
        |"EVENT_DTTM":"2022-11-05 17:58:51",
        |"KBO":"53276",
        |"AMOUNT":234,
        |"DELAY_TIME":1667660331000,
        |"PART_SUMM":34.5,
        |"TRANSFER_SUM":150,
        |"TARGET_CARD_NUMBER":"orc sam",
        |"TARGET_ACCOUNT":"1234567891******516",
        |"BANK_NAME":"afadg",
        |"SOURCE_CARD_NUMBER":"3636356",
        |"SOURCE_ACCOUNT":"1114567891******555",
        |"RECEIVER_BANK_SBP_ID":"CLASSIC",
        |"RECEIVER_NAME":"receiverNameSASD",
        |"TARGET_NAME":"asenderName",
        |"TRANSFER_WAY":"TestinteractionChanneldas"
        |}"""
      .stripMargin
      .replaceAll("\n", "")
      .replaceAll("\r", "")
  }

}
