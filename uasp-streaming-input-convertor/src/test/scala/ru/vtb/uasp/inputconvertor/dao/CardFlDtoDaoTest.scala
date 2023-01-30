package ru.vtb.uasp.inputconvertor.dao

import io.qameta.allure.Feature
import io.qameta.allure.scalatest.AllureScalatestContext
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.config.ConfigUtils.{getAllProps, getPropsFromResourcesFile, getSchemaKey, getStringFromResourceFile}
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.CardFlDtoDaoTest.{getCommonMessageAndProps, getCommonNullMessageAndProps}
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.factory.{UaspDtoParser, UaspDtoParserFactory}
import ru.vtb.uasp.inputconvertor.service.MsgCollector
import ru.vtb.uasp.inputconvertor.service.TransformHelper.extractJson
import ru.vtb.uasp.validate.DroolsValidator

class CardFlDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The result UaspDto" should "be valid" in new AllureScalatestContext {

    val (commonMessage, _, uaspDtoType, dtoMap, _) = getCommonMessageAndProps()
    println("commonMessage: " + commonMessage)
    val uaspDtoParser: UaspDtoParser = UaspDtoParserFactory(uaspDtoType, null)
    val uaspDto: UaspDto = uaspDtoParser.fromJValue(commonMessage.json_message.get, dtoMap)
    println("uaspDto: " + uaspDto)
    val standardUaspDto: UaspDto = UaspDtostandardFactory("cardfl").getstandardUaspDto

    assert(standardUaspDto == uaspDto.copy(process_timestamp = 0,
      uuid = ""))
  }

  "The result UaspDto" should "be valid with null" in new AllureScalatestContext {

    val (commonMessage, _, uaspDtoType, dtoMap, _) = getCommonNullMessageAndProps()
    println("commonMessage: " + commonMessage)
    val uaspDtoParser: UaspDtoParser = UaspDtoParserFactory(uaspDtoType, null)
    val uaspDto: UaspDto = uaspDtoParser.fromJValue(commonMessage.json_message.get, dtoMap)
    println("uaspDto: " + uaspDto)
    val standardUaspDto: UaspDto = UaspDtostandardFactory("cardflNull").getstandardUaspDto

    assert(standardUaspDto == uaspDto.copy(process_timestamp = 0,
      uuid = ""))
  }
}

object CardFlDtoDaoTest {
  def getCommonMessageAndProps(args: Array[String] = Array[String]()): (CommonMessageType, Map[String, String], String, Map[String, Array[String]], DroolsValidator) = {
    val allProps = getAllProps(args, "application-cardfl.properties")
    println(allProps)
    val uaspDtoType = allProps("app.uaspdto.type")
    println("uaspDtoType: " + uaspDtoType)

    val defaultJsonSchemaKey = getSchemaKey(allProps)
    println("defaultJsonSchemaKey: " + defaultJsonSchemaKey)

    val jsonMessageStr = getStringFromResourceFile(uaspDtoType + "-test.json")
    //val jsonMessageStr = getStringFromResourceFile ( "way4-ift-mes1.json" )
    println("jsonMessageStr: " + jsonMessageStr)

    val inMessage = InputMessageType(message_key = "123", message = jsonMessageStr.getBytes, Map[String, String]())
    println("inMessage: " + inMessage)
    val msgCollector = new MsgCollector
    extractJson(inMessage, allProps, defaultJsonSchemaKey, msgCollector)
    val uaspDtoMap = Map[String, String]() ++ getPropsFromResourcesFile(uaspDtoType + "-uaspdto.properties").get
    val dtoMap = uaspDtoMap.map(m => (m._1, m._2.split("::")))
    (msgCollector.getAll().get(0), allProps, uaspDtoType, dtoMap, new DroolsValidator(uaspDtoType + "-validation-rules.drl"))
  }

  def getCommonNullMessageAndProps(args: Array[String] = Array[String]()): (CommonMessageType, Map[String, String], String, Map[String, Array[String]], DroolsValidator) = {
    val allProps = getAllProps(args, "application-cardfl.properties")
    println(allProps)
    val uaspDtoType = allProps("app.uaspdto.type")
    println("uaspDtoType: " + uaspDtoType)

    val defaultJsonSchemaKey = getSchemaKey(allProps)
    println("defaultJsonSchemaKey: " + defaultJsonSchemaKey)

    val jsonMessageStr = getStringFromResourceFile(uaspDtoType + "-null-test.json")
    println("jsonMessageStr: " + jsonMessageStr)

    val inMessage = InputMessageType(message_key = "123", message = jsonMessageStr.getBytes, Map[String, String]())
    println("inMessage: " + inMessage)
    val msgCollector = new MsgCollector
    extractJson(inMessage, allProps, defaultJsonSchemaKey, msgCollector)
    val uaspDtoMap = Map[String, String]() ++ getPropsFromResourcesFile(uaspDtoType + "-uaspdto.properties").get
    val dtoMap = uaspDtoMap.map(m => (m._1, m._2.split("::")))
    (msgCollector.getAll().get(0), allProps, uaspDtoType, dtoMap, new DroolsValidator(uaspDtoType + "-validation-rules.drl"))
  }
}



