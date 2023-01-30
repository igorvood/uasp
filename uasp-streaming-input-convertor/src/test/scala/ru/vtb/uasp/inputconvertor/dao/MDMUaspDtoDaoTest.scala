package ru.vtb.uasp.inputconvertor.dao

import io.qameta.allure.{Allure, Feature}
import io.qameta.allure.scalatest.AllureScalatestContext
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.MDMUaspDtoDaoTest.getCommonMessageAndProps
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.factory.UaspDtoParserFactory
import ru.vtb.uasp.inputconvertor.service.MsgCollector
import ru.vtb.uasp.inputconvertor.service.TransformHelper.extractJson
import ru.vtb.uasp.validate.DroolsValidator
import ru.vtb.uasp.common.utils.config.ConfigUtils.{getAllProps, getPropsFromResourcesFile, getSchemaKey, getStringFromResourceFile}
import ru.vtb.uasp.inputconvertor.utils.config.{InputPropsModel, NewInputPropsModel}

@Feature("MDMUaspDtoDaoTest")
class MDMUaspDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The test data" should "be equals standard mdm UaspDto instance" in new AllureScalatestContext {
    Allure.link("291129", "manual", "")
    Allure.tms("21", "")

    val (commonMessage, allProps, uaspDtoType, dtoMap, droolsValidator) = getCommonMessageAndProps()
    println("commonMessage: " + commonMessage)
    val uaspDtoParser = UaspDtoParserFactory(uaspDtoType, null)
    val uaspDto: UaspDto = uaspDtoParser.fromJValue(commonMessage.json_message.get, dtoMap)
    println("uaspDto: " + uaspDto)
    val standardUaspDto = UaspDtostandardFactory("mdm").getstandardUaspDto
    assert(standardUaspDto == uaspDto.copy(process_timestamp = 0))
  }
}

object MDMUaspDtoDaoTest {
  def getCommonMessageAndProps(args: Array[String] = Array[String]()): (CommonMessageType,NewInputPropsModel, String, Map[String, Array[String]], DroolsValidator) = {
//    val allProps = getAllProps(args, "application-mdm.properties")
val allProps :  NewInputPropsModel = null
    println(allProps)
    val uaspDtoType = allProps.appUaspdtoType//("app.uaspdto.type")
    println("uaspDtoType: " + uaspDtoType)


    val jsonMessageStr = getStringFromResourceFile(uaspDtoType + "-test.json")
    //val jsonMessageStr = getStringFromResourceFile("mdm-test.json")
    println("jsonMessageStr: " + jsonMessageStr)

    val inMessage = InputMessageType(message_key = "123", message = jsonMessageStr.getBytes, Map[String, String]())
    println("inMessage: " + inMessage)
    val msgCollector = new MsgCollector
    extractJson(inMessage, allProps,  msgCollector)
    val uaspDtoMap = Map[String, String]() ++ getPropsFromResourcesFile(uaspDtoType + "-uaspdto.properties").get
    val dtoMap = uaspDtoMap.map(m => (m._1, m._2.split("::")))
    (msgCollector.getAll().get(0), allProps, uaspDtoType, dtoMap, new DroolsValidator(uaspDtoType + "-validation-rules.drl"))
  }
}
