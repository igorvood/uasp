package ru.vtb.uasp.common.mask.fun


class PassportNumberInStrMaskServiceTest extends AbstractMaskedTest {

  val maskService = PassportNumberInStrMaskService()


  val testCases = Map(
    "12 345678" -> "12 3***78",
  )

}
