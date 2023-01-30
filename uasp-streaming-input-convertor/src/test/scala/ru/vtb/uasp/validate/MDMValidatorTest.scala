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
    val (commonMessage, allProps, uaspDtoType, dtoMap, droolsValidator) = MDMProfileUaspDtoDaoTest.getCommonMessageAndProps()
    val uaspDtoParser = UaspDtoParserFactory(uaspDtoType, null)
    val uaspDto: UaspDto = uaspDtoParser.fromJValue(commonMessage.json_message.get, dtoMap)
    (uaspDto, droolsValidator)
  }

  "The valid UaspDto message" should "return empty error list" in new AllureScalatestContext {
    Allure.link("302204", "manual", "")
    Allure.tms("24", "")

    println("uaspDto: " + uaspDto)
    val result = validator.validate(List(uaspDto))
    result shouldBe empty
  }

  "The invalid UaspDto message with empty global_id" should "one error message" in new AllureScalatestContext {
    Allure.link("302206", "manual", "")
    Allure.tms("25", "")

    val invaligDataString = uaspDto.dataString ++ Map("global_id" -> "")
    val invalidUaspDto: UaspDto = uaspDto.copy(dataString = invaligDataString)
    println("uaspDto: " + invalidUaspDto)
    val result = validator.validate(List(invalidUaspDto))
    println("result: " + result)
    result should have size 1
  }

  "The invalid UaspDto message with empty global_id and local_id fields" should "two error messages" in new AllureScalatestContext {
    Allure.link("302207", "manual", "")
    Allure.tms("26", "")

    val invaligDataString = uaspDto.dataString ++ Map("global_id" -> "") ++ Map("local_id" -> "")
    val invalidUaspDto: UaspDto = uaspDto.copy(dataString = invaligDataString)
    println("uaspDto: " + invalidUaspDto)
    val result = validator.validate(List(invalidUaspDto))
    println("result: " + result)
    result should have size 2
  }


}