package ru.vtb.uasp.inputconvertor.dao

import io.qameta.allure.scalatest.AllureScalatestContext
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.common.utils.config.ConfigUtils.{getAllProps, getPropsFromResourcesFile, getSchemaKey, getStringFromResourceFile}
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.dao.LoyaltyUaspDtoDaoTest.getCommonMessageAndProps
import ru.vtb.uasp.inputconvertor.factory.{UaspDtoParser, UaspDtoParserFactory}
import ru.vtb.uasp.inputconvertor.service.MsgCollector
import ru.vtb.uasp.inputconvertor.service.TransformHelper.extractJson
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel
import ru.vtb.uasp.validate.DroolsValidator

class LoyaltyUaspDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The result UaspDto" should "be valid and drools return 1 error" in new AllureScalatestContext {

    val (commonMessage, _, uaspDtoType, dtoMap, validator) = getCommonMessageAndProps()
    println("commonMessage: " + commonMessage)
    val uaspDtoParser: UaspDtoParser = UaspDtoParserFactory(uaspDtoType, InputPropsModel(Map("input-convertor.test.uaspdto.type" -> uaspDtoType,
      "input-convertor-sys.test.card.number.sha256.salt" -> "TEST"), "test"))
    val uaspDto: UaspDto = uaspDtoParser.fromJValue(commonMessage.json_message.get, dtoMap)
    println("uaspDto: " + uaspDto)
    val standardUaspDto: UaspDto = UaspDtostandardFactory("loyalty").getstandardUaspDto

    assert(standardUaspDto == uaspDto.copy(process_timestamp = 0,
      uuid = ""))
    val valid=validator.validate(List(uaspDto))
    assert(valid.size == 1)
  }
}
object LoyaltyUaspDtoDaoTest {
  def getCommonMessageAndProps(args: Array[String] = Array[String]()): (CommonMessageType, Map[String, String], String, Map[String, Array[String]], DroolsValidator) = {
    val allProps = getAllProps(args, "application-loyalty.properties")
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
}




