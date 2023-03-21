package ru.vtb.uasp.inputconvertor.dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.CommonMsgAndProps.jsValueByType
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

class IssuingAccountUaspDtoTest extends AnyFlatSpec with should.Matchers {
  "The test data" should "be equals standard way4 UaspDto instance" in {

    val allProps: InputPropsModel = new InputPropsModel(
      serviceData = null,
      uaspdtoType = "issuing-account",
      consumerProp = null,
      outputSink = null,
      dlqSink = null,
      readSourceTopicFromBeginning = true,
      sha256salt = "",
      messageJsonPath = None,
      1,
      jsonSplitElement = None)

    val commonMessage = jsValueByType(allProps.uaspdtoType)

    val uaspDto: UaspDto = allProps.uaspDtoParser.fromJValue(commonMessage, allProps.dtoMap).head.get

    val standardUaspDto = UaspDtostandardFactory("issuing-account").getstandardUaspDto(uaspDto.uuid).copy(process_timestamp = uaspDto.process_timestamp)
    standardUaspDto shouldEqual uaspDto
  }
}
