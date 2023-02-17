package ru.vtb.uasp.inputconvertor.dao

import io.qameta.allure.scalatest.AllureScalatestContext
import io.qameta.allure.{Allure, Feature}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.config.ConfigUtils.getStringFromResourceFile
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.Way4UaspDtoDaoTest.getCommonMessageAndProps
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.service.TransformHelper.extractJson
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

@Feature("Way4UaspDtoDaoTest")
class Way4UaspDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The test data" should "be equals standard way4 UaspDto instance" in new AllureScalatestContext {
    Allure.link("291128", "manual", "")
    Allure.tms("17", "")

    val (commonMessage, allProp) = getCommonMessageAndProps()

    val uaspDto: UaspDto = allProp.uaspDtoParser.fromJValue(commonMessage.json_message.get, allProp.dtoMap)
    private val dto: UaspDto = uaspDto.copy(
      dataString = uaspDto.dataString - ("card_ps_funding_source", "transaction_currency", "card_masked_pan",
        "source_account_w4", "base_currency_w4", "source_system_w4"),
      dataDecimal = uaspDto.dataDecimal - "base_amount_w4")

    val standardUaspDto: UaspDto = UaspDtostandardFactory("way4").getstandardUaspDto(uaspDto.uuid)
    assert(standardUaspDto.copy(dataString = standardUaspDto.dataString + ("card_expire_w4" -> "2607", "payment_scheme_w4" -> "Mastercard",
      "processing_date_string" -> "2021-07-18T18:12:24Z", "terminal_id" -> "11111111"), process_timestamp = dto.process_timestamp)
      == dto)
  }
}

object Way4UaspDtoDaoTest {
  def getCommonMessageAndProps(args: Array[String] = Array[String]()): (CommonMessageType, InputPropsModel) = {
    val allProps: InputPropsModel = new InputPropsModel(
      serviceData = null,
      uaspdtoType = "way4",
      consumerProp = null,
      outputSink = null,
      dlqSink = null,
      readSourceTopicFromBeginning = true,
      sha256salt = null,
      messageJsonPath = None,
      jsonSplitElement = None)
    val uaspDtoType = allProps.uaspdtoType

    val jsonMessageStr = getStringFromResourceFile(uaspDtoType + "-test.json")

    val inMessage = InputMessageType(message_key = "123", message = jsonMessageStr.getBytes)
    val msgCollector = new MsgCollector
    extractJson(inMessage, allProps, msgCollector)
    (msgCollector.getAll().get(0).right.get, allProps)
  }
}


