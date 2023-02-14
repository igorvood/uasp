package ru.vtb.uasp.common.mask.fun

class EmailStrMaskedServiceTest extends AbstractMaskedTest {


  override val maskService: JsStringMaskedFun = EMailMask()

  override val testCases: Map[String, String] = Map(
    "qwerty@some.com" -> "q*****@some.com",
    "qwerty" -> "q*****",
    "" -> "",
  )

}
