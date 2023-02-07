package ru.vtb.uasp.common.mask

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json.{JsValue, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.mask.MaskedPredef.{MaskJsValuePredef, PathFactory}


class MaskedTest extends AnyFlatSpec with should.Matchers {

  "mask all existing fields " should " OK" in {

    val dto = UaspDto(
      id = "1",
      dataInt = Map("2" -> 2),
      dataLong = Map("3" -> 3),
      dataFloat = Map("4" -> 4),
      dataDouble = Map("5" -> 5),
      dataDecimal = Map("6" -> 6),
      dataString = Map("7" -> "7"),
      dataBoolean = Map("8" -> true),
      uuid = "uuid",
      process_timestamp = 18
    )

    val jsObject: JsValue = Json.toJsObject(dto)

    val path = Map(
      "id" -> "ru.vtb.uasp.common.mask.fun.StringMaskAll",
      "dataInt.2" -> "ru.vtb.uasp.common.mask.fun.NumberMaskAll",
      "dataLong.3" -> "ru.vtb.uasp.common.mask.fun.NumberMaskAll",
      "dataFloat.4" -> "ru.vtb.uasp.common.mask.fun.NumberMaskAll",
      "dataDouble.5" -> "ru.vtb.uasp.common.mask.fun.NumberMaskAll",
      "dataDecimal.6" -> "ru.vtb.uasp.common.mask.fun.NumberMaskAll",
      "dataString.7" -> "ru.vtb.uasp.common.mask.fun.StringMaskAll",
      "dataBoolean.8" -> "ru.vtb.uasp.common.mask.fun.BooleanMaskAll",

    )
      .map(q => MaskedStrPathWithFunName(q._1, q._2))
      .toJsonPath()
      .right.get

    val value1 = jsObject.toMaskedJson(path).right.get

    val value2 = value1.validate[UaspDto]


    val dto1 = dto.copy(
      id = "***MASKED***",
      dataInt = Map("2" -> 0),
      dataLong = Map("3" -> 0),
      dataFloat = Map("4" -> 0),
      dataDouble = Map("5" -> 0),
      dataDecimal = Map("6" -> 0),
      dataString = Map("7" -> "***MASKED***"),
      dataBoolean = Map("8" -> false),
      process_timestamp = value2.get.process_timestamp)

    assertResult(dto1)(value2.get)

  }


}
