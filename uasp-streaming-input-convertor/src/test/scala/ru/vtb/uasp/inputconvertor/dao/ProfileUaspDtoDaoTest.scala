package ru.vtb.uasp.inputconvertor.dao

import io.qameta.allure.Feature
import io.qameta.allure.scalatest.AllureScalatestContext
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.config.ConfigUtils.{getPropsFromResourcesFile, getStringFromResourceFile}
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.ProfileUaspDtoDaoTest.getCommonMessageAndProps
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.factory.UaspDtoParserFactory
import ru.vtb.uasp.inputconvertor.service.ConvertOutMapService
import ru.vtb.uasp.inputconvertor.service.TransformHelper.extractJson
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel
import ru.vtb.uasp.validate.DroolsValidator

@Feature("FirstSalaryUaspDtoDaoTest")
class ProfileUaspDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The test data" should "be equals standard first salary UaspDto instance" in new AllureScalatestContext {

    val (commonMessage, allProps, uaspDtoType, dtoMap, droolsValidator) = getCommonMessageAndProps()

    val uaspDtoParser = UaspDtoParserFactory(uaspDtoType, allProps)
    val uaspDto: UaspDto = uaspDtoParser.fromJValue(commonMessage.json_message.get, dtoMap)

    val standardUaspDto = UaspDtostandardFactory("profile").getstandardUaspDto(uaspDto.uuid).copy(process_timestamp = uaspDto.process_timestamp)
    val validationList = droolsValidator.validate(List(uaspDto))

    validationList shouldBe empty
    assert(uaspDto == standardUaspDto)

    val convertOutMapService = new ConvertOutMapService
    val serializable = convertOutMapService.serialize(uaspDto)

  }

}

object ProfileUaspDtoDaoTest {

  def getCommonMessageAndProps(args: Array[String] = Array[String]()): (CommonMessageType, InputPropsModel, String, Map[String, Array[String]], DroolsValidator) = {
    val allProps: InputPropsModel = new InputPropsModel(
      null,
      "profile",
      null,
      null,
      null,
      false,
      null,
      null,
      true,
      "",
      None,
      None)

    val uaspDtoType = allProps.uaspdtoType

    val jsonMessageStr = getStringFromResourceFile(uaspDtoType + "-test.json")

    val inMessage = InputMessageType(message_key = "CFT2RS.10000033307567", message = jsonMessageStr.getBytes, Map[String, String]())

    val msgCollector = new MsgCollector
    extractJson(inMessage, allProps, msgCollector)
    val uaspDtoMap = Map[String, String]() ++ getPropsFromResourcesFile(uaspDtoType + "-uaspdto.properties").get
    val dtoMap = uaspDtoMap.map(m => (m._1, m._2.split("::")))
    (msgCollector.getAll().get(0), allProps, uaspDtoType, dtoMap, new DroolsValidator(uaspDtoType + "-validation-rules.drl"))
  }
}




