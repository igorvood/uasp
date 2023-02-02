package ru.vtb.uasp.inputconvertor.dao

import io.qameta.allure.scalatest.AllureScalatestContext
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.config.ConfigUtils.getStringFromResourceFile
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.IssuingAccountBalanceUaspDtoTest.getCommonMessageAndProps
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.service.TransformHelper.extractJson
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

class IssuingAccountBalanceUaspDtoTest extends AnyFlatSpec with should.Matchers {
  "The test data" should "be equals standard way4 UaspDto instance" in new AllureScalatestContext {
    val (commonMessage, allProps) = getCommonMessageAndProps()


    val uaspDto: UaspDto = allProps.uaspDtoParser.fromJValue(commonMessage.json_message.get, allProps.dtoMap)

    val standardUaspDto = UaspDtostandardFactory("issuing-account-balance").getstandardUaspDto(uaspDto.uuid).copy(process_timestamp = uaspDto.process_timestamp)
    standardUaspDto shouldEqual uaspDto
  }

}

object IssuingAccountBalanceUaspDtoTest {
  def getCommonMessageAndProps(args: Array[String] = Array[String]()): (CommonMessageType, InputPropsModel) = {

    val allProps: InputPropsModel = new InputPropsModel(
      serviceName = null,
      uaspdtoType = "issuing-account-balance",
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
    (msgCollector.getAll().get(0), allProps)
  }

}