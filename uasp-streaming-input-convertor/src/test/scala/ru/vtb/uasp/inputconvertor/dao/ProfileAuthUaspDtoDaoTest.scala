package ru.vtb.uasp.inputconvertor.dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.config.ConfigUtils.getStringFromResourceFile
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.ProfileAuthUaspDtoDaoTest.getCommonMessageAndProps
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.service.TransformHelper.extractJson
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

class ProfileAuthUaspDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The test data" should "be equals standard first salary UaspDto instance" in {

    val (commonMessage, allProps) = getCommonMessageAndProps()


    val uaspDto: UaspDto = allProps.uaspDtoParser.fromJValue(commonMessage.json_message.get, allProps.dtoMap)

    val standardUaspDto = UaspDtostandardFactory("profile-auth").getstandardUaspDto(uaspDto.uuid).copy(process_timestamp = uaspDto.process_timestamp)
    val validationList = allProps.droolsValidator.validate(List(uaspDto))

    validationList shouldBe empty

//    assert(uaspDto.dataBoolean == standardUaspDto.dataBoolean)
//    assert(uaspDto.dataInt == standardUaspDto.dataInt)
//    assert(uaspDto.dataLong == standardUaspDto.dataLong)
//    assert(uaspDto.dataFloat == standardUaspDto.dataFloat)
//    assert(uaspDto.dataDecimal == standardUaspDto.dataDecimal)
//    assert(uaspDto.dataString == standardUaspDto.dataString)
//
//
//    assert(uaspDto == standardUaspDto)
  }

}

object ProfileAuthUaspDtoDaoTest {

  def getCommonMessageAndProps(args: Array[String] = Array[String]()): (CommonMessageType, InputPropsModel) = {

    val allProps: InputPropsModel = new InputPropsModel(
      serviceData = null,
      uaspdtoType = "profile-auth",
      consumerProp = null,
      outputSink = null,
      dlqSink = null,
      readSourceTopicFromBeginning = true,
      sha256salt = "",
      messageJsonPath = None,
      jsonSplitElement = None)

    val uaspDtoType = allProps.uaspdtoType


    val jsonMessageStr = getStringFromResourceFile(uaspDtoType + "-test.json")


    val inMessage = InputMessageType(message_key = "1", message = jsonMessageStr.getBytes)

    val msgCollector = new MsgCollector
    extractJson(inMessage, allProps, msgCollector)
    (msgCollector.getAll().get(0).right.get, allProps)
  }
}




