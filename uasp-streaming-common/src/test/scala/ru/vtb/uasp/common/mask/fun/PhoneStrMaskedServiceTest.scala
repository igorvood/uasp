package ru.vtb.uasp.common.mask.fun

class PhoneStrMaskedServiceTest extends AbstractMaskedTest {


  override val maskService: JsStringMaskedFun = PhoneStrMaskService()

  override val testCases: Map[String, String] = Map(
    "+7 965 123 45 67" -> "+7*************67",
    "+7-965-123-45-67" -> "+7*************67",
    "+79651234567" -> "+7*********67",
  )


}
