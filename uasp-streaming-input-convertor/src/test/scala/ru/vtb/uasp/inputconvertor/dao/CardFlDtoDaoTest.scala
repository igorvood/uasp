package ru.vtb.uasp.inputconvertor.dao

import io.qameta.allure.scalatest.AllureScalatestContext
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.config.ConfigUtils.getStringFromResourceFile
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.CardFlDtoDaoTest.{getCommonMessageAndProps, getCommonNullMessageAndProps}
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.service.TransformHelper.extractJson
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

class CardFlDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The result UaspDto" should "be valid" in new AllureScalatestContext {

    val (commonMessage, allProp) = getCommonMessageAndProps()

    val uaspDto: UaspDto = allProp.uaspDtoParser.fromJValue(commonMessage.json_message.get, allProp.dtoMap)

    val standardUaspDto: UaspDto = UaspDtostandardFactory("cardfl").getstandardUaspDto(uaspDto.uuid).copy(process_timestamp = uaspDto.process_timestamp)

    assert(standardUaspDto == uaspDto)
  }

  "The result UaspDto" should "be valid with null" in new AllureScalatestContext {

    val (commonMessage, allProp) = getCommonNullMessageAndProps()


    val uaspDto: UaspDto = allProp.uaspDtoParser.fromJValue(commonMessage.json_message.get, allProp.dtoMap)

    val standardUaspDto: UaspDto = UaspDtostandardFactory("cardflNull").getstandardUaspDto(uaspDto.uuid).copy(process_timestamp = uaspDto.process_timestamp)

    assert(standardUaspDto == uaspDto)
  }
}

object CardFlDtoDaoTest {
  def getCommonMessageAndProps(args: Array[String] = Array[String]()): (CommonMessageType, InputPropsModel) = {

    val allProps: InputPropsModel = new InputPropsModel(
      serviceData = null,
      uaspdtoType = "cardfl",
      consumerProp = null,
      outputSink = null,
      dlqSink = null,
      readSourceTopicFromBeginning = true,
      sha256salt = "",
      messageJsonPath = None,
      jsonSplitElement = None)


    val uaspDtoType = allProps.uaspdtoType

    val jsonMessageStr = getStringFromResourceFile(uaspDtoType + "-test.json")

    val inMessage = InputMessageType(message_key = "123", message = jsonMessageStr.getBytes)

    val msgCollector = new MsgCollector
    extractJson(inMessage, allProps, msgCollector)
    (msgCollector.getAll().get(0).right.get, allProps)
  }

  def getCommonNullMessageAndProps(args: Array[String] = Array[String]()): (CommonMessageType, InputPropsModel) = {
    val allProps: InputPropsModel = new InputPropsModel(
      serviceData = null,
      uaspdtoType = "cardfl",
      consumerProp = null,
      outputSink = null,
      dlqSink = null,
      readSourceTopicFromBeginning = true,
      sha256salt = "",
      messageJsonPath = None,
      jsonSplitElement = None)

    val uaspDtoType = allProps.uaspdtoType

    val jsonMessageStr = getStringFromResourceFile(uaspDtoType + "-null-test.json")

    val inMessage = InputMessageType(message_key = "123", message = jsonMessageStr.getBytes)

    val msgCollector = new MsgCollector
    extractJson(inMessage, allProps, msgCollector)
    (msgCollector.getAll().get(0).right.get, allProps)
  }
}



