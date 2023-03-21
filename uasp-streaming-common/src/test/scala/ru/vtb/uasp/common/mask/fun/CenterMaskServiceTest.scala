package ru.vtb.uasp.common.mask.fun

class CenterMaskServiceTest extends AbstractMaskedTest {

  override val maskService: JsStringMaskedFun = new CenterMaskService("2","3")

  override val testCases: Map[String, String] = Map(
    "" -> "",
    "1" -> "*",
    "12" -> "**",
    "123" -> "***",
    "1234" -> "****",
    "12345" -> "*****",
    "123456" -> "12*456",
    "1234567" -> "12**567",
  )

  "mask with cntEnd = 0 " should " OK" in {
    val service = new CenterMaskService("2", "0")
    assert("12*****"==service.mask("1234567").value)

  }

  "mask with cntBegin = 0 " should " OK" in {
    val service = new CenterMaskService("0", "2")
    assert("*****67" == service.mask("1234567").value)

  }

}
