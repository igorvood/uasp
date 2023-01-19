package ru.vtb.uasp.vector.dsl

import org.scalatest.flatspec.AnyFlatSpec
import ru.vtb.uasp.common.dto.UaspDto

class CustomFunctionHandlerTest extends AnyFlatSpec {
  private val uasp = UaspDto(
    "123",
    Map.empty,
    Map.empty,
    Map.empty,
    Map.empty,
    Map.empty,
    Map("mdmID" -> "AZ12345"),
    Map.empty,
    "",
    0L
  )

  "CustomFunctionHandler None " should "be equals" in {
    assertResult(CustomFunctionHandler.customFunctions("None")("Test", uasp))("Test")
    assertResult(CustomFunctionHandler.customFunctions("None")(2, uasp))(2)
    assertResult(CustomFunctionHandler.customFunctions("None")(4.6, uasp))(4.6)
    assertResult(CustomFunctionHandler.customFunctions("None")(BigDecimal(123), uasp))(BigDecimal(123))
    assertResult(CustomFunctionHandler.customFunctions("None")(32L, uasp))(32L)
    assertResult(CustomFunctionHandler.customFunctions("None")(32, uasp))(32)
    assertResult(CustomFunctionHandler.customFunctions("None")(true, uasp))(true)
  }

}
