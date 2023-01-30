package ru.vtb.uasp.inputconvertor.dao

import io.qameta.allure.Feature
import io.qameta.allure.scalatest.AllureScalatestContext
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.config.ConfigUtils.{getAllProps, getPropsFromResourcesFile, getSchemaKey, getStringFromResourceFile}
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.ProfileUaspDtoDaoTest.getCommonMessageAndProps
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.factory.UaspDtoParserFactory
import ru.vtb.uasp.inputconvertor.service.TransformHelper.extractJson
import ru.vtb.uasp.inputconvertor.service.{ConvertOutMapService, MsgCollector}
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel
import ru.vtb.uasp.validate.DroolsValidator

@Feature("FirstSalaryUaspDtoDaoTest")
class ProfileUaspDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The test data" should "be equals standard first salary UaspDto instance" in new AllureScalatestContext {
    /*Allure.link("291129", "manual", "")
    Allure.tms("21", "")*/

    val (commonMessage, allProps, uaspDtoType, dtoMap, droolsValidator) = getCommonMessageAndProps()
    println("commonMessage: " + commonMessage)
    val uaspDtoParser = UaspDtoParserFactory(uaspDtoType, InputPropsModel(Map("input-convertor.uaspdto.type" -> uaspDtoType), ""))
    val uaspDto: UaspDto = uaspDtoParser.fromJValue(commonMessage.json_message.get, dtoMap)
    println("uaspDto: " + uaspDto)
    val standardUaspDto = UaspDtostandardFactory("profile").getstandardUaspDto
    val validationList = droolsValidator.validate(List(uaspDto))

    validationList shouldBe empty
    assert(standardUaspDto == uaspDto.copy(process_timestamp = 0, uuid = ""))

    val convertOutMapService = new ConvertOutMapService
    val serializable = convertOutMapService.serialize(uaspDto)

  }

}

object ProfileUaspDtoDaoTest {

  def getCommonMessageAndProps(args: Array[String] = Array[String]()): (CommonMessageType, Map[String, String], String, Map[String, Array[String]], DroolsValidator) = {
    val allProps = getAllProps(args, "application-profile.properties")
    println(allProps)
    val uaspDtoType = allProps("app.uaspdto.type")
    println("uaspDtoType: " + uaspDtoType)

    val defaultJsonSchemaKey = getSchemaKey(allProps)
    println("defaultJsonSchemaKey: " + defaultJsonSchemaKey)

    val jsonMessageStr = getStringFromResourceFile(uaspDtoType + "-test.json")
    //val jsonMessageStr = getStringFromResourceFile("mdm-test.json")
    println("jsonMessageStr: " + jsonMessageStr)

    val inMessage = InputMessageType(message_key = "CFT2RS.10000033307567", message = jsonMessageStr.getBytes, Map[String, String]())
    println("inMessage: " + inMessage)
    val msgCollector = new MsgCollector
    extractJson(inMessage, allProps, defaultJsonSchemaKey, msgCollector)
    val uaspDtoMap = Map[String, String]() ++ getPropsFromResourcesFile(uaspDtoType + "-uaspdto.properties").get
    val dtoMap = uaspDtoMap.map(m => (m._1, m._2.split("::")))
    (msgCollector.getAll().get(0), allProps, uaspDtoType, dtoMap, new DroolsValidator(uaspDtoType + "-validation-rules.drl"))
  }
}




