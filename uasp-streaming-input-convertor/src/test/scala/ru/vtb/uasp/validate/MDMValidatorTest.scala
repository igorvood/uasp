package ru.vtb.uasp.validate

import io.qameta.allure.scalatest.AllureScalatestContext
import io.qameta.allure.{Allure, Feature}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.MDMProfileUaspDtoDaoTest
import ru.vtb.uasp.inputconvertor.factory.UaspDtoParserFactory

@Feature("MDMValidateTest")
class MDMValidateTest extends AnyFlatSpec with should.Matchers {
  val (uaspDto, validator) = {
    val (commonMessage, allProps) = MDMProfileUaspDtoDaoTest.getCommonMessageAndProps()
    val uaspDtoParser = UaspDtoParserFactory(allProps)
    val uaspDto: UaspDto = uaspDtoParser.fromJValue(commonMessage.json_message.get, allProps.dtoMap)
    (uaspDto, allProps.droolsValidator)
  }

  "The valid UaspDto message" should "return empty error list" in new AllureScalatestContext {

    val result = validator.validate(List(uaspDto))
    result shouldBe empty
  }

  "The invalid UaspDto message with empty global_id" should "one error message" in new AllureScalatestContext {

    val invaligDataString = uaspDto.dataString ++ Map("global_id" -> "")
    val invalidUaspDto: UaspDto = uaspDto.copy(dataString = invaligDataString)
    val result = validator.validate(List(invalidUaspDto))
    result should have size 1
  }

  "The invalid UaspDto message with empty global_id and local_id fields" should "two error messages" in new AllureScalatestContext {

    val invaligDataString = uaspDto.dataString ++ Map("global_id" -> "") ++ Map("local_id" -> "")
    val invalidUaspDto: UaspDto = uaspDto.copy(dataString = invaligDataString)

    val result = validator.validate(List(invalidUaspDto))

    result should have size 2
  }


}