package ru.vtb.uasp.inputconvertor.dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.CommonMsgAndProps.jsValueByType
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

class UddsUaspDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The result UaspDto" should "be valid" in {
    val inputPropsModel: InputPropsModel = new InputPropsModel(
      serviceData = null,
      uaspdtoType = "udds",
      consumerProp = null,
      outputSink = null,
      dlqSink = null,
      readSourceTopicFromBeginning = true,
      sha256salt = "",
      messageJsonPath = None, 1,
      jsonSplitElement = None)

    val commonMessage = jsValueByType(inputPropsModel.uaspdtoType)

    val uaspDto: UaspDto = inputPropsModel.uaspDtoParser.fromJValue(commonMessage, inputPropsModel.dtoMap).head.get

    val standardUaspDto: UaspDto = UaspDtostandardFactory("udds").getstandardUaspDto(uaspDto.uuid).copy(process_timestamp = uaspDto.process_timestamp)

    assert(standardUaspDto == uaspDto)
  }
}
