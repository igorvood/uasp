package ru.vtb.uasp.inputconvertor.dao

import io.qameta.allure.scalatest.AllureScalatestContext
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.config.ConfigUtils.getStringFromResourceFile
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.CACardFlDtoDaoTest.getCommonMessageAndProps
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.service.TransformHelper.extractJson
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

class CACardFlDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The result UaspDto" should "be valid" in new AllureScalatestContext {

    val (commonMessage, allProps) = getCommonMessageAndProps()

    val uaspDto: UaspDto = allProps.uaspDtoParser.fromJValue(commonMessage.json_message.get, allProps.dtoMap)

    val standardUaspDto: UaspDto = UaspDtostandardFactory(allProps.uaspdtoType).getstandardUaspDto(uaspDto.uuid).copy(process_timestamp = uaspDto.process_timestamp)

    assert(standardUaspDto == uaspDto)
  }

}

object CACardFlDtoDaoTest {
  def getCommonMessageAndProps(args: Array[String] = Array[String]()): (CommonMessageType, InputPropsModel) = {

    val allProps: InputPropsModel = new InputPropsModel(
      serviceData = null,
      uaspdtoType = "ca-cardfl",
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
    (msgCollector.getAll().get(0), allProps)
  }

}



