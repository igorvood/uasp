package ru.vtb.uasp.validate

import io.qameta.allure.{Allure, Feature}
import io.qameta.allure.scalatest.AllureScalatestContext
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.Way4UaspDtoDaoTest
import ru.vtb.uasp.inputconvertor.factory.UaspDtoParserFactory
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

@Feature("Way4ValidateTest")
class Way4ValidateTest extends AnyFlatSpec with should.Matchers {

  val (uaspDto, validator) = {
    val (commonMessage, allProps, uaspDtoType, dtoMap, droolsValidator) = Way4UaspDtoDaoTest.getCommonMessageAndProps()
    val uaspDtoParser = UaspDtoParserFactory(uaspDtoType, null)
    val uaspDto: UaspDto = uaspDtoParser.fromJValue(commonMessage.json_message.get, dtoMap)
    (uaspDto, droolsValidator)
  }

  "The invalid UaspDto message with empty id" should "return one error message" in new AllureScalatestContext {
    Allure.link("302200", "manual", "")
    Allure.tms("19", "")

    val invalidUaspDto: UaspDto = uaspDto.copy(id = "")
    println("uaspDto: " + invalidUaspDto)
    val result = validator.validate(List(invalidUaspDto))
    println("result: " + result)
    assert(result.size == 1 && result.head.msg == "The client ID must be mandatory")
  }

  "The valid UaspDto message" should "return empty error list" in new AllureScalatestContext {
    Allure.link("302201", "manual", "")
    Allure.tms("20", "")

    println("uaspDto: " + uaspDto)
    val result = validator.validate(List(uaspDto))
    result shouldBe empty
  }

  "The UaspDTO with transaction_amount has invalid format" should "return error message" in new AllureScalatestContext {
    val invalidUaspDto: UaspDto = uaspDto.copy(dataDecimal = Map("transaction_amount" -> 1636.1200000000001))
    val result = validator.validate(List(invalidUaspDto))
    println("result: " + result)
    assert(result.size == 1 && result.head.msg.contains("Transaction amount has invalid format"))
  }

  "The UaspDTO with transaction_amount has valid format" should "return empty error list" in new AllureScalatestContext {
    val invalidUaspDto: UaspDto = uaspDto.copy(dataDecimal = Map("transaction_amount" -> BigDecimal.exact("123456789123456789.12345")))
    val result = validator.validate(List(invalidUaspDto))
    println("result: " + result)
    result shouldBe empty
  }

}
