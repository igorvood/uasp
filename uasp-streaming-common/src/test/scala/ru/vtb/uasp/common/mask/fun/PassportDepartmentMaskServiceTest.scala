package ru.vtb.uasp.common.mask.fun

class PassportDepartmentMaskServiceTest extends AbstractMaskedTest {

  override val maskService: JsStringMaskedFun = PassportDepartmentMaskService()
  override val testCases: Map[String, String] = Map(
    "Отделом милиции №12 УВД №12 г. Петропавлоск-Камчатский" -> "Отделом милиц ***** лоск-Камчатский",
  )
}
