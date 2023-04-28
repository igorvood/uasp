package ru.vtb.uasp.common.mask.fun

class SummaDotMaskServiceTest extends AbstractMaskedTest {


  override val maskService: JsStringMaskedFun = SummaMaskService()

  override val testCases: Map[String, String] = Map(
    "" -> "*",
    "1" -> "*",
    "12" -> "*",
    "12.3" -> "*.3",
    "12.34" -> "*.34",
  )


}