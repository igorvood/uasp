package ru.vtb.uasp.inputconvertor.validate

import io.qameta.allure.Feature
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json.Json
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonMsgAndProps.jsValueByType
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel


@Feature("Way4ValidateTest")
class Way4ValidateTest extends AnyFlatSpec with should.Matchers {

  val (uaspDto, validator) = {

    val allProps: InputPropsModel = new InputPropsModel(
      serviceData = null,
      uaspdtoType = "way4",
      consumerProp = null,
      outputSink = null,
      dlqSink = null,
      readSourceTopicFromBeginning = true,
      sha256salt = null,
      messageJsonPath = None, 1,
      jsonSplitElement = None)

    val commonMessage = jsValueByType(allProps.uaspdtoType)

    //    val uaspDto: UaspDto = allProps.uaspDtoParser.fromJValue(commonMessage.json_message, allProps.dtoMap).get

    //TODO тут поправить
    val uaspDto = allProps.uaspDtoParser.fromJValue(commonMessage, allProps.dtoMap).head.get
    //val uaspDto =  UaspDto("sad", Map(),Map(),Map(),Map(),Map(),Map(),Map(),"asd", 9)

    (Json.fromJson[UaspDto](uaspDto).get, allProps.droolsValidator)
  }

  "The invalid UaspDto message with empty id" should "return one error message" in {

    val invalidUaspDto: UaspDto = uaspDto.copy(id = "")
    val result = validator.validate(List(invalidUaspDto))
    assert(result.size == 1 && result.head.msg == "The client ID must be mandatory")
  }

  "The valid UaspDto message" should "return empty error list" in {
    val result = validator.validate(List(uaspDto))
    result shouldBe empty
  }

  "The UaspDTO with transaction_amount has invalid format" should "return error message" in {
    val invalidUaspDto: UaspDto = uaspDto.copy(dataDecimal = Map("transaction_amount" -> 1636.1200000000001))
    val result = validator.validate(List(invalidUaspDto))
    assert(result.size == 1 && result.head.msg.contains("Transaction amount has invalid format"))
  }

  "The UaspDTO with transaction_amount has valid format" should "return empty error list" in {
    val invalidUaspDto: UaspDto = uaspDto.copy(dataDecimal = Map("transaction_amount" -> BigDecimal.exact("123456789123456789.12345")))
    val result = validator.validate(List(invalidUaspDto))
    result shouldBe empty
  }

}
