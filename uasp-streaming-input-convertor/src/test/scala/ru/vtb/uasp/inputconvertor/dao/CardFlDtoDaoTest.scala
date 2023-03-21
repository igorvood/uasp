package ru.vtb.uasp.inputconvertor.dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json.{JsValue, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.config.ConfigUtils.getStringFromResourceFile
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.CommonMsgAndProps.jsValueByType
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

class CardFlDtoDaoTest extends AnyFlatSpec with should.Matchers {

  val allProp: InputPropsModel = new InputPropsModel(
    serviceData = null,
    uaspdtoType = "cardfl",
    consumerProp = null,
    outputSink = null,
    dlqSink = null,
    readSourceTopicFromBeginning = true,
    sha256salt = "",
    messageJsonPath = None,
    1,
    jsonSplitElement = None)

  "The result UaspDto" should "be valid" in {

    val commonMessage = jsValueByType(allProp.uaspdtoType)

    val uaspDto: UaspDto = allProp.uaspDtoParser.fromJValue(commonMessage, allProp.dtoMap).head.get

    val standardUaspDto: UaspDto = UaspDtostandardFactory("cardfl").getstandardUaspDto(uaspDto.uuid).copy(process_timestamp = uaspDto.process_timestamp)

    assert(standardUaspDto == uaspDto)
  }

  "The result UaspDto" should "be valid with null" in {


    val commonMessage = jsValueByType(allProp.uaspdtoType)

    val jsonMessageStr = getStringFromResourceFile("cardfl" + "-null-test.json")

    val value1: JsValue = Json.parse(jsonMessageStr)


    val uaspDto: UaspDto = allProp.uaspDtoParser.fromJValue(value1, allProp.dtoMap).head.get

    val standardUaspDto: UaspDto = UaspDtostandardFactory("cardflNull").getstandardUaspDto(uaspDto.uuid).copy(process_timestamp = uaspDto.process_timestamp)

    assert(standardUaspDto == uaspDto)
  }
}

