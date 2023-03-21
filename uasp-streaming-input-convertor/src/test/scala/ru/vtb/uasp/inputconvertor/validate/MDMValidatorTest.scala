package ru.vtb.uasp.inputconvertor.validate

import io.qameta.allure.Feature
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonMsgAndProps.jsValueByType
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel


@Feature("MDMValidateTest")
class MDMValidateTest extends AnyFlatSpec with should.Matchers {
  val (uaspDto, validator) = {

    val allProps: InputPropsModel = new InputPropsModel(
      null,
      "mdm-profile",
      null,
      null,
      null,
      readSourceTopicFromBeginning = true,
      sha256salt = null,
      messageJsonPath = None, 1,
      jsonSplitElement = None)

    val commonMessage = jsValueByType(allProps.uaspdtoType)


    //    val uaspDto: UaspDto = allProps.uaspDtoParser.fromJValue(commonMessage.json_message, allProps.dtoMap).get
    //TODO тут поправить

    val uaspDto = allProps.uaspDtoParser.fromJValue(commonMessage, allProps.dtoMap).head.get

    (uaspDto, allProps.droolsValidator)
  }

  "The valid UaspDto message" should "return empty error list" in {

    val result = validator.validate(List(uaspDto))
    result shouldBe empty
  }

  "The invalid UaspDto message with empty global_id" should "one error message" in {

    val invaligDataString = uaspDto.dataString ++ Map("global_id" -> "")
    val invalidUaspDto: UaspDto = uaspDto.copy(dataString = invaligDataString)
    val result = validator.validate(List(invalidUaspDto))
    result should have size 1
  }

  "The invalid UaspDto message with empty global_id and local_id fields" should "two error messages" in {

    val invaligDataString = uaspDto.dataString ++ Map("global_id" -> "") ++ Map("local_id" -> "")
    val invalidUaspDto: UaspDto = uaspDto.copy(dataString = invaligDataString)

    val result = validator.validate(List(invalidUaspDto))

    result should have size 2
  }


}