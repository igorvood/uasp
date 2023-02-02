package ru.vtb.uasp.inputconvertor.dao

import io.qameta.allure.Feature
import io.qameta.allure.scalatest.AllureScalatestContext
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.config.ConfigUtils.getStringFromResourceFile
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.WithdrawWay4UaspDtoDaoTest.getCommonMessageAndProps
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.factory.{UaspDtoParser, UaspDtoParserFactory}
import ru.vtb.uasp.inputconvertor.service.TransformHelper.extractJson
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

@Feature("WithdrawWay4UaspDtoDaoTest")
class WithdrawWay4UaspDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The result UaspDto" should "be contains fields tagged_data_source_pay" in new AllureScalatestContext {

    val (commonMessage, allProp) = getCommonMessageAndProps()
    val uaspDtoParser: UaspDtoParser = UaspDtoParserFactory(allProp)
    val uaspDto: UaspDto = uaspDtoParser.fromJValue(commonMessage.json_message.get, allProp.dtoMap)
    val standardUaspDto: UaspDto = UaspDtostandardFactory("way4").getstandardUaspDto(uaspDto.uuid)

    private val dto: UaspDto = uaspDto.copy(

      dataString = uaspDto.dataString - ("card_ps_funding_source", "card_masked_pan", "transaction_currency"))


    val expecteduaspDto = standardUaspDto.copy(id = "1493661370",
      dataLong = standardUaspDto.dataLong + ("transaction_datetime" -> 1655828503000L, "processing_datetime" -> 1655817703000L, "effective_date" -> 1655251200000L),
      dataDecimal = Map("transaction_amount" -> 1000.00, "base_amount_w4" -> -1000.00),
      dataString = standardUaspDto.dataString + ("source_system_w4" -> "WAY4",
        "source_account_w4" -> "40817810569166560", "base_currency_w4" -> "RUR",
        "audit_rrn" -> "217200452175", "operation_id" -> "A95691218370G569166560",
        "mcc" -> "6051", "audit_srn" -> "M1724298H6H1", "service_type" -> "J6",
        "audit_auth_code" -> "500005", "local_id" -> "1493661370", "terminal_type" -> "ECOMMERCE",
        "tagged_data_KBO" -> "643", "tagged_data_source_pay" -> "UNKNOWN", "merchant_name_w4" -> "Visa Unique",
        "processing_date_string" -> "2022-06-21T13:21:43Z", "terminal_id" -> "10000015"), process_timestamp = dto.process_timestamp)
    assert(expecteduaspDto == dto)
  }
}

object WithdrawWay4UaspDtoDaoTest {
  def getCommonMessageAndProps(args: Array[String] = Array[String]()): (CommonMessageType, InputPropsModel) = {
    val allProps: InputPropsModel = new InputPropsModel(
      serviceName = null,
      uaspdtoType = "way4-withdraw",
      consumerProp = null,
      outputSink = null,
      dlqSink = null,
      readSourceTopicFromBeginning = true,
      sha256salt = null,
      messageJsonPath = None,
      jsonSplitElement = None)
    val uaspDtoType = allProps.uaspdtoType

    val jsonMessageStr = getStringFromResourceFile(uaspDtoType + "-test.json")

    val inMessage = InputMessageType(message_key = "123", message = jsonMessageStr.getBytes, Map[String, String]())
    val msgCollector = new MsgCollector
    extractJson(inMessage, allProps, msgCollector)
    (msgCollector.getAll().get(0), allProps)
  }
}

