package ru.vtb.uasp.inputconvertor.dao

import io.qameta.allure.Feature
import io.qameta.allure.scalatest.AllureScalatestContext
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.config.ConfigUtils.{getPropsFromResourcesFile, getStringFromResourceFile}
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.PosTransactionWay4UaspDtoDaoTest.getCommonMessageAndProps
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.factory.{UaspDtoParser, UaspDtoParserFactory}
import ru.vtb.uasp.inputconvertor.service.TransformHelper.extractJson
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel
import ru.vtb.uasp.validate.DroolsValidator

@Feature("PosTerminalWay4UaspDtoDaoTest")
class PosTransactionWay4UaspDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The result UaspDto" should "be contains fields card_ps_funding_source, card_masked_pan  and transaction_currency" in new AllureScalatestContext {

    val (commonMessage, _, uaspDtoType, dtoMap, _) = getCommonMessageAndProps()

    val uaspDtoParser: UaspDtoParser = UaspDtoParserFactory(uaspDtoType, null)
    val uaspDto: UaspDto = uaspDtoParser.fromJValue(commonMessage.json_message.get, dtoMap)

    private val dto: UaspDto = uaspDto.copy(dataString = uaspDto.dataString - ("source_account_w4", "base_currency_w4"))

    val standardUaspDto: UaspDto = UaspDtostandardFactory("way4").getstandardUaspDto(uaspDto.uuid)
    val expecteduaspDto = standardUaspDto.copy(id = uaspDto.id,
      dataLong = standardUaspDto.dataLong + ("transaction_datetime" -> 1655814114000L, "processing_datetime" -> 1655803314000L, "effective_date" -> 1655424000000L),
      dataDecimal = Map("transaction_amount" -> 1000.00, "base_amount_w4" -> -1000.00),
      dataString = standardUaspDto.dataString +
        ("card_ps_funding_source" -> "Credit", "card_masked_pan" -> "529938******8812", "transaction_currency" -> "RUR",
          "terminal_type" -> "POS", "card_masked_pan" -> "427230******3991", "card_expire_w4" -> "2406",
          "audit_rrn" -> "217200451688", "operation_id" -> "A95691215730G569482840", "mcc" -> "5462", "audit_srn" -> "M1724298H2QH",
          "audit_auth_code" -> "500004", "local_id" -> "1493814790", "source_system_w4" -> "WAY4", "merchant_name_w4" -> "Visa Retail",
          "processing_date_string" -> "2022-06-21T09:21:54Z"), process_timestamp = dto.process_timestamp)
    assert(dto == expecteduaspDto)
  }
}

object PosTransactionWay4UaspDtoDaoTest {
  def getCommonMessageAndProps(args: Array[String] = Array[String]()): (CommonMessageType, InputPropsModel, String, Map[String, Array[String]], DroolsValidator) = {
    val allProps: InputPropsModel = new InputPropsModel(
      serviceName = null,
      uaspdtoType = "pos-transaction",
      consumerProp = null,
      outputSink = null,
      dlqSink = null,
      readSourceTopicFromBeginning = true,
      sha256salt = "",
      messageJsonPath = None,
      jsonSplitElement = None)

    val uaspDtoType = allProps.uaspdtoType

    val jsonMessageStr = getStringFromResourceFile(uaspDtoType + "-test.json")

    val inMessage = InputMessageType(message_key = "123", message = jsonMessageStr.getBytes, Map[String, String]())

    val msgCollector = new MsgCollector
    extractJson(inMessage, allProps, msgCollector)
    val uaspDtoMap = Map[String, String]() ++ getPropsFromResourcesFile(uaspDtoType + "-uaspdto.properties").get
    val dtoMap = uaspDtoMap.map(m => (m._1, m._2.split("::")))
    (msgCollector.getAll().get(0), allProps, uaspDtoType, dtoMap, new DroolsValidator(uaspDtoType + "-validation-rules.drl"))
  }
}

