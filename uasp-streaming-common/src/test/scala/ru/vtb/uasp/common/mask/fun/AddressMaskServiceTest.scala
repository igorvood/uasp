package ru.vtb.uasp.common.mask.fun

class AddressMaskServiceTest extends AbstractMaskedTest {


  override val maskService: JsStringMaskedFun = AddressMaskService()

  override val testCases: Map[String, String] = Map(
    "Спас-Угол Калязинского уезда Тверской губернии дом 15" -> "Спас-Угол Калязинского уезда Тверской губернии д***",
  )


}
