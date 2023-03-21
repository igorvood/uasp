package ru.vtb.uasp.inputconvertor.dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.CommonMsgAndProps.jsValueByType
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

class LoyaltyUaspDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The result UaspDto" should "be valid and drools return 1 error" in {

    val inputPropsModel: InputPropsModel = new InputPropsModel(
      serviceData = null,
      uaspdtoType = "loyalty",
      consumerProp = null,
      outputSink = null,
      dlqSink = null,
      readSourceTopicFromBeginning = true,
      sha256salt = "",
      messageJsonPath = None,
      1,
      jsonSplitElement = None)

    val commonMessage = jsValueByType(inputPropsModel.uaspdtoType)

    val uaspDto: UaspDto = inputPropsModel.uaspDtoParser.fromJValue(commonMessage, inputPropsModel.dtoMap).head.get

    val standardUaspDto: UaspDto = UaspDtostandardFactory("loyalty").getstandardUaspDto(uaspDto.uuid).copy(process_timestamp = uaspDto.process_timestamp)

    assert(uaspDto == standardUaspDto)
    val valid = inputPropsModel.droolsValidator.validate(List(uaspDto))
    assert(valid.size == 1)
  }
}

