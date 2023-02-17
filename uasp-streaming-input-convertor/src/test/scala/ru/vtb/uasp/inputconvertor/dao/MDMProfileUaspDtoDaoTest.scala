package ru.vtb.uasp.inputconvertor.dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.config.ConfigUtils.getStringFromResourceFile
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.MDMProfileUaspDtoDaoTest.getCommonMessageAndProps
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.service.TransformHelper.extractJson
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

class MDMProfileUaspDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The test data" should "be equals standard mdm-profile UaspDto instance" in {

    val (commonMessage, allProps) = getCommonMessageAndProps()

    val uaspDto: UaspDto = allProps.uaspDtoParser.fromJValue(commonMessage.json_message.get, allProps.dtoMap)

    val standardUaspDto = UaspDtostandardFactory("mdm").getstandardUaspDto(uaspDto.uuid)
    val expectedUaspDto = standardUaspDto.copy(id = "456", dataString = Map("local_id" -> "456", "global_id" -> "10324", "system_number" -> "99995"), process_timestamp = uaspDto.process_timestamp)
    assert(expectedUaspDto == uaspDto)
  }
}

object MDMProfileUaspDtoDaoTest {
  def getCommonMessageAndProps(args: Array[String] = Array[String]()): (CommonMessageType, InputPropsModel) = {
    val allProps: InputPropsModel = new InputPropsModel(
      null,
      "mdm-profile",
      null,
      null,
      null,
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
