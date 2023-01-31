package ru.vtb.uasp.inputconvertor.dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.config.ConfigUtils.{getPropsFromResourcesFile, getStringFromResourceFile}
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.MDMProfileUaspDtoDaoTest.getCommonMessageAndProps
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.factory.UaspDtoParserFactory
import ru.vtb.uasp.inputconvertor.service.TransformHelper.extractJson
import ru.vtb.uasp.inputconvertor.utils.config.NewInputPropsModel
import ru.vtb.uasp.validate.DroolsValidator

class MDMProfileUaspDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The test data" should "be equals standard mdm-profile UaspDto instance" in {

    val (commonMessage, allProps, uaspDtoType, dtoMap, droolsValidator) = getCommonMessageAndProps()
    val uaspDtoParser = UaspDtoParserFactory(uaspDtoType, null)
    val uaspDto: UaspDto = uaspDtoParser.fromJValue(commonMessage.json_message.get, dtoMap)

    val standardUaspDto = UaspDtostandardFactory("mdm").getstandardUaspDto(uaspDto.uuid)
    val expectedUaspDto = standardUaspDto.copy(id = "456", dataString = Map("local_id" -> "456", "global_id" -> "10324", "system_number" -> "99995"), process_timestamp = uaspDto.process_timestamp)
    assert(expectedUaspDto == uaspDto)
  }
}

object MDMProfileUaspDtoDaoTest {
  def getCommonMessageAndProps(args: Array[String] = Array[String]()): (CommonMessageType, NewInputPropsModel, String, Map[String, Array[String]], DroolsValidator) = {
    val allProps: NewInputPropsModel = new NewInputPropsModel(
      null,
      "mdm-profile",
      null,
      null,
      null,
      false,
      null,
      null,
      true,
      null,
      None,
      None)
    val uaspDtoType = allProps.appUaspdtoType //("app.uaspdto.type")


    val jsonMessageStr = getStringFromResourceFile(uaspDtoType + "-test.json")
    //val jsonMessageStr = getStringFromResourceFile("mdm-test.json")

    val inMessage = InputMessageType(message_key = "123", message = jsonMessageStr.getBytes, Map[String, String]())
    val msgCollector = new MsgCollector
    extractJson(inMessage, allProps, msgCollector)
    val uaspDtoMap = Map[String, String]() ++ getPropsFromResourcesFile(uaspDtoType + "-uaspdto.properties").get
    val dtoMap = uaspDtoMap.map(m => (m._1, m._2.split("::")))
    (msgCollector.getAll().get(0), allProps, uaspDtoType, dtoMap, new DroolsValidator(uaspDtoType + "-validation-rules.drl"))
  }
}
