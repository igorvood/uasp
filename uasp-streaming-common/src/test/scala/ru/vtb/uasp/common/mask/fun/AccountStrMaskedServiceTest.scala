package ru.vtb.uasp.common.mask.fun

class AccountStrMaskedServiceTest extends AbstractMaskedTest {


  override val maskService: JsStringMaskedFun = AccountMask()

  override val testCases: Map[String, String] = Map(
    "12345678901234567890" -> "1234567890******7890",
//    "qwerty" -> "q*****",
//    "" -> "",
  )

}
