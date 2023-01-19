package ru.vtb.uasp.vector.dsl

import org.scalatest.flatspec.AnyFlatSpec
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.vector.dsl.dto.{MapType, RuleField, TransformedData}

class RuleHandlerTest extends AnyFlatSpec {
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

  "RuleValueHandlerTest  " should "be equals" in {
    assertResult(RuleHandler.getMapByTypeAndName(uasp, "mdmID", MapType.STRING, true))("AZ12345")
  }

  "8765  " should "be equals" in {
    assertResult(RuleHandler.castValueToTargetType(4.5.toString, MapType.DECIMAL))(BigDecimal(4.5))
  }

  "324567  " should "be equals" in {
    val rf = RuleField(true, "mdmID", MapType.STRING, "mdmID", MapType.STRING, "None")
    assertResult(RuleHandler.transformRuleField(uasp, rf))(TransformedData("AZ12345", rf))

  }
}
