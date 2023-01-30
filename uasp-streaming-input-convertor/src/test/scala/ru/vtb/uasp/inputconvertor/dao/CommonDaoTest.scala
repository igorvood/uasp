package ru.vtb.uasp.inputconvertor.dao

import io.qameta.allure.Feature
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

@Feature("CommonDaoTest")
class CommonDaoTest extends AnyFlatSpec with should.Matchers {

  "findAndMaskNumber " should "be return masked card number" in {
    //given
    val cardNumber = "7777888899990001"
    val expected = "777788******0001"
    //when
    val result = CommonDao.findAndMaskNumber(cardNumber)

    //then
    result should not be null
    result.contains('*') shouldBe true
    result shouldBe expected
  }
}
