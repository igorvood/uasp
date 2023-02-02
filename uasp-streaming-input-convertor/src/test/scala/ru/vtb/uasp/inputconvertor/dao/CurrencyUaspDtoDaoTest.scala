package ru.vtb.uasp.inputconvertor.dao

import io.qameta.allure.Feature
import io.qameta.allure.scalatest.AllureScalatestContext
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.config.ConfigUtils.getStringFromResourceFile
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.CurrencyUaspDtoDaoTest.getCommonMessageAndProps
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.factory.UaspDtoParserFactory
import ru.vtb.uasp.inputconvertor.service.TransformHelper.extractJson
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

@Feature("CurrencyUaspDtoDaoTest")
class CurrencyUaspDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The test data" should "be equals standard currency UaspDto instance" in new AllureScalatestContext {

    val (commonMessage, allProps) = getCommonMessageAndProps()

    val uaspDtoParser = UaspDtoParserFactory(allProps)
    val uaspDto: UaspDto = uaspDtoParser.fromJValue(commonMessage.json_message.get, allProps.dtoMap)

    val standardUaspDto = UaspDtostandardFactory("currency").getstandardUaspDto(uaspDto.uuid).copy(process_timestamp = uaspDto.process_timestamp)
    assert(standardUaspDto == uaspDto)
  }

}

object CurrencyUaspDtoDaoTest {
  def getCommonMessageAndProps(args: Array[String] = Array[String]()): (CommonMessageType, InputPropsModel) = {

    val allProps: InputPropsModel = new InputPropsModel(
      serviceName = null,
      uaspdtoType = "currency",
      consumerProp = null,
      outputSink = null,
      dlqSink = null,
      readSourceTopicFromBeginning = true,
      sha256salt = "",
      messageJsonPath = None,
      jsonSplitElement = None)

    val uaspDtoType = allProps.uaspdtoType

    val jsonMessageStr = getStringFromResourceFile(uaspDtoType + "-test.json")

    val inMessage = InputMessageType(message_key = "78a3b0e4-4221-3020-8bad-080de84de4c6", message = jsonMessageStr.getBytes, Map[String, String]())

    val msgCollector = new MsgCollector
    extractJson(inMessage, allProps, msgCollector)
    (msgCollector.getAll().get(0), allProps)
  }
}

