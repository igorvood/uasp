package ru.vtb.uasp.vector.cases

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.libs.json.{JsObject, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.vector.dsl.RuleParser
import ru.vtb.uasp.vector.service.RuleApplyMapFunction
import ru.vtb.uasp.vector.util.TestUtil.testHarnessCreator


class RuleCase48Test extends AnyFlatSpec {
  private val packageServiceProcessFunction = new RuleApplyMapFunction

  behavior of "RuleCase48Test"

  private val uasp = UaspDto(
    "1234ID",
    Map.empty,
    Map("transmission_dttm" -> 1667660331000L),
    Map.empty,
    Map.empty,
    Map.empty,
    Map(
      "account_type" -> "AccountTestType",
      "source_system_prf" -> "Profile",
      "system-classification" -> "RTO_48_case",
      "msgid" -> "op_id",
      "OPERATION_INFO" -> "edfrghjk",
      "account_dep" -> "437276438756238742565",
      "transferOrderId" -> "sdaf32wq2",
      "card_number" -> "523436",
      "TRANSACTION_INFO" -> "523436",
      "exchange_currency" -> "RUR",
      "card_expiration_dt" -> "2271",
      "contract_card_type_cd" -> "CLASSIC",
      "package_nm" -> "CLASSIC",
      "receiverFpsBankId" -> "CLASSIC",
      "receiverName" -> "receiverNameSASD",
      "senderName" -> "asenderName",
      "interactionChannel" -> "TestinteractionChanneldas",
      "ACCOUNT_NM" -> "Some account nm",
      "PRODUCT_NAME" -> "PRODUCT_NAME PRODUCT_NAME nm",
      "card_expiration_dt" -> "2030/04",
      "processing_resolution" -> "orc sam",
      "targetMaskedPan" -> "orc sam",
      "targetAccount" -> "234",
      "targetBankRussianName" -> "afadg",
      "sourceMaskedPan" -> "3636356",
      "sourceAccount" -> "23636356",
      "kbo" -> "53276",
      "account_name" -> "25424524625",
      "payment_system_desc" -> "Visa",
      "terminal_owner" -> "Mira",
      "OPERATION_STATUS" -> "Sale",
      "card_type_cd" -> "DEBET",
      "POS_FLG" -> "Y",
      "salary_card_type_flg" -> "Y",
      "source_acc" -> "1234567891012141516",
      "exchange_currency" -> "RUR",
      "categoryName" -> "Магазин"
    ),
    Map.empty,
    "",
    0L)

  it should "Case 48 should be ok" in {
    val testHarness = testHarnessCreator(packageServiceProcessFunction, uasp)

    val packageServiceDlq = testHarness.getSideOutput(RuleApplyMapFunction.dlqRuleApplyMapFunction)
    packageServiceDlq shouldBe null

    val packageServiceOutDto = testHarness.getSideOutput(RuleParser.tags("RTO_48_case"))
    val result = Json.parse(new String(packageServiceOutDto.peek().getValue.value)).as[JsObject]
    result.-("KAFKA_DTTM").toString() shouldBe
    """{
        |"MDM_ID":"1234ID",
        |"OPERATION_ID":"op_id",
        |"EVENT_DTTM":"2022-11-05 17:58:51",
        |"CONTACTLESS_NM":"Mira",
        |"PAYMENT_SYSTEM":"Visa",
        |"CARD_TYPE":"DEBET",
        |"PRODUCT_NM":"",
        |"PACKAGE_NM":"CLASSIC",
        |"SALARY_FLG":"Y",
        |"OPERATION_STATUS":"Sale",
        |"SYSTEM_DEVICE":"",
        |"CARD_NUMBER":"523436",
        |"EXPIRE_CARD_DT":"30/04"
        |}"""
      .stripMargin
      .replaceAll("\n", "")
      .replaceAll("\r", "")
  }

}
