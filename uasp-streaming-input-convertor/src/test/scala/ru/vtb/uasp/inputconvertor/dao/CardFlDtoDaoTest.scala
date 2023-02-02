package ru.vtb.uasp.inputconvertor.dao

import io.qameta.allure.scalatest.AllureScalatestContext
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.config.ConfigUtils.{getPropsFromResourcesFile, getStringFromResourceFile}
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.CardFlDtoDaoTest.{getCommonMessageAndProps, getCommonNullMessageAndProps}
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.factory.{UaspDtoParser, UaspDtoParserFactory}
import ru.vtb.uasp.inputconvertor.service.TransformHelper.extractJson
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel
import ru.vtb.uasp.validate.DroolsValidator

class CardFlDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The result UaspDto" should "be valid" in new AllureScalatestContext {

    val (commonMessage, _, uaspDtoType, dtoMap, _) = getCommonMessageAndProps()
    println("commonMessage: " + commonMessage)
    val uaspDtoParser: UaspDtoParser = UaspDtoParserFactory(uaspDtoType, null)
    val uaspDto: UaspDto = uaspDtoParser.fromJValue(commonMessage.json_message.get, dtoMap)
    println("uaspDto: " + uaspDto)
    val standardUaspDto: UaspDto = UaspDtostandardFactory("cardfl").getstandardUaspDto(uaspDto.uuid).copy(process_timestamp = uaspDto.process_timestamp)

    assert(standardUaspDto == uaspDto)
  }

  "The result UaspDto" should "be valid with null" in new AllureScalatestContext {

    val (commonMessage, _, uaspDtoType, dtoMap, _) = getCommonNullMessageAndProps()

    val uaspDtoParser: UaspDtoParser = UaspDtoParserFactory(uaspDtoType, null)
    val uaspDto: UaspDto = uaspDtoParser.fromJValue(commonMessage.json_message.get, dtoMap)

    val standardUaspDto: UaspDto = UaspDtostandardFactory("cardflNull").getstandardUaspDto(uaspDto.uuid).copy(process_timestamp = uaspDto.process_timestamp)

    assert(standardUaspDto == uaspDto)
  }
}

object CardFlDtoDaoTest {
  def getCommonMessageAndProps(args: Array[String] = Array[String]()): (CommonMessageType, InputPropsModel, String, Map[String, Array[String]], DroolsValidator) = {

    val allProps: InputPropsModel = new InputPropsModel(
      null,
      "cardfl",
      null,
      null,
      null,
      false,
      null,
      true,
      "",
      None,
      None)


    val uaspDtoType = allProps.uaspdtoType

    val jsonMessageStr = getStringFromResourceFile(uaspDtoType + "-test.json")

    val inMessage = InputMessageType(message_key = "123", message = jsonMessageStr.getBytes, Map[String, String]())

    val msgCollector = new MsgCollector
    extractJson(inMessage, allProps, msgCollector)
    val uaspDtoMap = Map[String, String]() ++ getPropsFromResourcesFile(uaspDtoType + "-uaspdto.properties").get
    val dtoMap = uaspDtoMap.map(m => (m._1, m._2.split("::")))
    (msgCollector.getAll().get(0), allProps, uaspDtoType, dtoMap, new DroolsValidator(uaspDtoType + "-validation-rules.drl"))
  }

  def getCommonNullMessageAndProps(args: Array[String] = Array[String]()): (CommonMessageType, InputPropsModel, String, Map[String, Array[String]], DroolsValidator) = {
    val allProps: InputPropsModel = new InputPropsModel(
      null,
      "cardfl",
      null,
      null,
      null,
      false,
      null,
      true,
      "",
      None,
      None)

    val uaspDtoType = allProps.uaspdtoType

    val jsonMessageStr = getStringFromResourceFile(uaspDtoType + "-null-test.json")

    val inMessage = InputMessageType(message_key = "123", message = jsonMessageStr.getBytes, Map[String, String]())

    val msgCollector = new MsgCollector
    extractJson(inMessage, allProps, msgCollector)
    val uaspDtoMap = Map[String, String]() ++ getPropsFromResourcesFile(uaspDtoType + "-uaspdto.properties").get
    val dtoMap = uaspDtoMap.map(m => (m._1, m._2.split("::")))
    (msgCollector.getAll().get(0), allProps, uaspDtoType, dtoMap, new DroolsValidator(uaspDtoType + "-validation-rules.drl"))
  }
}



