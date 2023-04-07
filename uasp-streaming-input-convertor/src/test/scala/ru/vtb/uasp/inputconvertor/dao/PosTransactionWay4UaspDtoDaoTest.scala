package ru.vtb.uasp.inputconvertor.dao

import io.qameta.allure.Feature
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json.Json
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.CommonMsgAndProps.jsValueByType
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

@Feature("PosTerminalWay4UaspDtoDaoTest")
class PosTransactionWay4UaspDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The result UaspDto" should "be contains fields card_ps_funding_source, card_masked_pan  and transaction_currency" in {

    val allProps: InputPropsModel = new InputPropsModel(
      serviceData = null,
      uaspdtoType = "pos-transaction",
      consumerProp = null,
      outputSink = null,
      dlqSink = null,
      readSourceTopicFromBeginning = true,
      sha256salt = "",
      messageJsonPath = None, 1,
      jsonSplitElement = None)

    val commonMessage = jsValueByType(allProps.uaspdtoType)

    val value1 = allProps.uaspDtoParser.fromJValue(commonMessage, allProps.dtoMap).head.get
    val uaspDto: UaspDto = Json.fromJson[UaspDto](value1).get

    val dto: UaspDto = uaspDto.copy(dataString = uaspDto.dataString - ("source_account_w4", "base_currency_w4"))

    val standardUaspDto: UaspDto = UaspDtostandardFactory("way4").getstandardUaspDto(uaspDto.uuid)
    val expecteduaspDto = standardUaspDto.copy(id = uaspDto.id,
      dataLong = standardUaspDto.dataLong + ("transaction_datetime" -> 1655814114000L, "processing_datetime" -> 1655803314000L, "effective_date" -> 1655424000000L),
      dataDecimal = Map("transaction_amount" -> 1000.00, "base_amount_w4" -> -1000.00),
      dataString = standardUaspDto.dataString +
        ("card_ps_funding_source" -> "Credit", "card_masked_pan" -> "529938******8812", "transaction_currency" -> "RUR",
          "terminal_type" -> "POS", "card_masked_pan" -> "427230******3991", "card_expire_w4" -> "2406",
          "audit_rrn" -> "217200451688", "operation_id" -> "A95691215730G569482840", "mcc" -> "5462", "audit_srn" -> "M1724298H2QH",
          "audit_auth_code" -> "500004", "local_id" -> "1493814790", "source_system_w4" -> "WAY4", "merchant_name_w4" -> "Visa Retail",
          //          "processing_date_string" -> "2022-06-21T09:21:54Z"
        ), process_timestamp = dto.process_timestamp)
    assert(dto == expecteduaspDto)
  }
}

