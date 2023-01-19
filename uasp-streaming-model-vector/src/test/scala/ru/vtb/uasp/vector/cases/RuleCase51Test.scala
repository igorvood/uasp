package ru.vtb.uasp.vector.cases

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import play.api.libs.json.{JsObject, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.vector.dsl.RuleParser
import ru.vtb.uasp.vector.service.RuleApplyMapFunction
import ru.vtb.uasp.vector.util.TestUtil.testHarnessCreator


class RuleCase51Test extends AnyFlatSpec {
  private val packageServiceProcessFunction = new RuleApplyMapFunction

  behavior of "RuleCase51Test"

  private val uasp = UaspDto(
    "1234ID",
    Map.empty,
    Map("transmission_dttm" -> 1667660331000L),
    Map.empty,
    Map.empty,
    Map("amount" -> BigDecimal(150)),
    Map(
      "account_type" -> "AccountTestType",
      "source_system_prf" -> "Profile",
      "system-classification" -> "RTO_51_case",
      "msgid" -> "op_id",
      "OPERATION_INFO" -> "edfrghjk",
      "TRANSACTION_INFO" -> "djhajkfak",
      "account_dep" -> "437276438756238742565",
      "transferOrderId" -> "sdaf32wq2",
      "card_number" -> "523436",
      "exchange_currency" -> "RUR",
      "package_nm" -> "CLASSIC",
      "account_num" -> "136786314891y398",
      "is_virtual_card_flg" -> "adfadf",
      "receiverFpsBankId" -> "CLASSIC",
      "receiverName" -> "receiverNameSASD",
      "senderName" -> "asenderName",
      "interactionChannel" -> "TestinteractionChanneldas",
      "ACCOUNT_NM" -> "Some account nm",
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
      "CARD_TYPE" -> "DEBET",
      "POS_FLG" -> "Y",
      "card_expiration_dt" -> "2033/04",
      "contract_card_type_cd" -> "1234567891012141516",
      "exchange_currency" -> "RUR",
      "contract_card_type_cd" -> "MRMCSMRRPS",
      "categoryName" -> "Магазин"
    ),
    Map.empty,
    "",
    0L)

  it should "Case 51 should be ok" in {
    val testHarness = testHarnessCreator(packageServiceProcessFunction, uasp)

    val packageServiceDlq = testHarness.getSideOutput(RuleApplyMapFunction.dlqRuleApplyMapFunction)
    packageServiceDlq shouldBe null

    val packageServiceOutDto = testHarness.getSideOutput(RuleParser.tags("RTO_51_case"))
    val result = Json.parse(new String(packageServiceOutDto.peek().getValue.value)).as[JsObject]
    result.-("KAFKA_DTTM").toString() shouldBe
    """{
        |"MDM_ID":"1234ID",
        |"OPERATION_ID":"op_id",
        |"EVENT_DTTM":"2022-11-05 17:58:51",
        |"SERVICE_NAME":"Mira",
        |"GROUP_SERVICE_NAME":"djhajkfak",
        |"SOURCE_CARD_NUMBER":"523436",
        |"SOURCE_ACCOUNT":"1367863148******",
        |"CARD_TYPE":"Пластиковая",
        |"PRODUCT_NAME":"Карта жителя Самарской области",
        |"SERV_PACK":"CLASSIC",
        |"VALIDITY":"04/33",
        |"OS_DEVICE":"",
        |"PAYMENT_SYSTEM":"Visa"
        |}"""
      .stripMargin
      .replaceAll("\n", "")
      .replaceAll("\r", "")
  }

}
