package ru.vtb.uasp.inputconvertor.dao

import io.qameta.allure.Feature
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json.Json
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.CommonMsgAndProps.jsValueByType
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

@Feature("FirstSalaryUaspDtoDaoTest")
class ProfileUaspDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The test data" should "be equals standard first salary UaspDto instance" in {
    val allProps: InputPropsModel = new InputPropsModel(
      serviceData = null,
      uaspdtoType = "profile",
      consumerProp = null,
      outputSink = null,
      dlqSink = null,
      readSourceTopicFromBeginning = true,
      sha256salt = "",
      messageJsonPath = None, 1,
      jsonSplitElement = None)

    val commonMessage = jsValueByType(allProps.uaspdtoType)

    val value1 = allProps.uaspDtoParser.fromJValue(commonMessage, allProps.dtoMap).head.get
    val uaspDto: UaspDto = Json.fromJson[UaspDto](value1).get

    val standardUaspDto = UaspDtostandardFactory("profile").getstandardUaspDto(uaspDto.uuid).copy(process_timestamp = uaspDto.process_timestamp)
    val validationList = allProps.droolsValidator.validate(List(uaspDto))

    validationList shouldBe empty
    assert(uaspDto == standardUaspDto)

  }

}
