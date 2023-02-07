package ru.vtb.uasp.common.mask

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json.{JsBoolean, JsNumber, JsObject, JsString, JsValue, Json}
import ru.vtb.uasp.common
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.mask.JsMaskedPath.{JsMaskedPathObject, PathFactory}

import scala.annotation.tailrec



class MaskedTest extends AnyFlatSpec with should.Matchers {

  def maskData(jsObject: JsValue, path: JsMaskedPath): JsValue = {
    val value1: JsValue = (jsObject, path) match {
      case (JsObject(values), JsMaskedPathObject(masked)) => {
        val newVals = values
          .map(vv => {
            val tuple: (String, JsValue) = masked
              .get(vv._1)
              .map(q => vv._1 -> maskData(vv._2, q))
              .getOrElse(vv)
            tuple
          }
          )
        JsObject(newVals)
      }
      case (JsString(value), JsStringMaskedPathValue(masked)) => masked.mask(value)
      case (JsNumber(value), JsNumberMaskedPathValue(masked)) => masked.mask(value)
      case (JsBoolean(value), JsBooleanMaskedPathValue(masked)) => masked.mask(value)
      case (q, w) => throw new IllegalArgumentException(s"Unable to masked value '$q' wrapper class  ${q.getClass} with function -> ${w.getClass}")
    }

    value1
  }

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
      "id" -> "ru.vtb.uasp.common.mask.StringMaskAll",
      "dataInt.2" -> "ru.vtb.uasp.common.mask.NumberMaskAll",
      "dataLong.3" -> "ru.vtb.uasp.common.mask.NumberMaskAll",
      "dataFloat.4" -> "ru.vtb.uasp.common.mask.NumberMaskAll",
      "dataDouble.5" -> "ru.vtb.uasp.common.mask.NumberMaskAll",
      "dataDecimal.6" -> "ru.vtb.uasp.common.mask.NumberMaskAll",
      "dataString.7" -> "ru.vtb.uasp.common.mask.StringMaskAll",
      "dataBoolean.8" -> "ru.vtb.uasp.common.mask.BooleanMaskAll",

    )
      .map(q => MaskedStrPathWithFunName(q._1, q._2))
      .toJsonPath()
      .right.get

    val value1 = maskData(jsObject, path)

    val value2 = value1.validate[UaspDto]


    val dto1 = dto.copy(
      id = "***MASKED***",
      dataInt = Map("2" -> 0),
      dataLong = Map("3" -> 0),
      dataFloat = Map("4" -> 0),
      dataDouble = Map("5" -> 0),
      dataDecimal = Map("6" -> 0),
      dataString = Map("7" -> "***MASKED***"),
      dataBoolean= Map("8" -> false),
      process_timestamp = value2.get.process_timestamp)

    assertResult(dto1)(value2.get)

  }



}
