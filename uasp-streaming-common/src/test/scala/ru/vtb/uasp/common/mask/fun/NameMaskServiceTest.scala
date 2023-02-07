package ru.vtb.uasp.common.mask.fun

class NameMaskServiceTest extends AbstractMaskedTest {


  override val maskService: JsStringMaskedFun = NameMaskService()

  override val testCases: Map[String, String] = Map(
    "Салтыков-Щедрин" -> "С***-Щ***",
    "Салтыков-Щедрин-Щедрин" -> "С***-Щ***-Щ***",
    "Салтыков Щедрин" -> "С*** Щ***",
    "Салтыков Щедрин Щедрин" -> "С*** Щ*** Щ***",

    "Велосипедист" -> "В***ст",
    "Иванов" -> "И*****",
    "Ли" -> "**",
    "Ли12" -> "****",
    "Ли123" -> "Л****",
  )


}
