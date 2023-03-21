package ru.vtb.uasp.inputconvertor.dao

import io.qameta.allure.Feature
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.CommonMsgAndProps.jsValueByType
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

@Feature("FirstSalaryUaspDtoDaoTest")
class FirstSalaryUaspDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The test data" should "be equals standard first salary UaspDto instance" in {

    val allProps: InputPropsModel = new InputPropsModel(
      serviceData = null,
      uaspdtoType = "first-salary",
      consumerProp = null,
      outputSink = null,
      dlqSink = null,
      readSourceTopicFromBeginning = true,
      sha256salt = "",
      messageJsonPath = None,
      1,
      jsonSplitElement = None)

    val commonMessage = jsValueByType(allProps.uaspdtoType)

    val head = allProps.uaspDtoParser.fromJValue(commonMessage, allProps.dtoMap).head
    val uaspDto: UaspDto = head.get

    val standardUaspDto = UaspDtostandardFactory("first-salary").getstandardUaspDto(uaspDto.uuid).copy(process_timestamp = uaspDto.process_timestamp)
    assert(uaspDto == standardUaspDto)
  }

}

