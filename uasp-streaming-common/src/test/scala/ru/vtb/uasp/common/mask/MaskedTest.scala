package ru.vtb.uasp.common.mask

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import ru.vtb.uasp.common
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.mask.JsMaskedPath.PathFactory

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
      case (JsString(value), JsStringMaskedPathValue(masked)) => masked.asdasd(value)
      case (q, w) => throw new IllegalArgumentException("bad 2 " + q.getClass + " -> "+w.getClass)
    }

    value1
  }

  "transform str to JPath " should " OK" in {

    val dto = UaspDto(
      id = "1",
      dataInt = Map("2" -> 2),
      dataLong = Map("3" -> 3),
      dataFloat = Map("4" -> 4),
      dataDouble = Map("5" -> 5),
      dataDecimal = Map("6" -> 6),
      dataString = Map("7" -> "7"),
      dataBoolean = Map("8" -> false),
      uuid = "uuid",
      process_timestamp = 18
    )

    val jsObject: JsValue = Json.toJsObject(dto)
//    println(jsObject)

    val path = Map(
      "id" -> "ru.vtb.uasp.common.mask.StringMaskAll",
      "dataString.7" -> "ru.vtb.uasp.common.mask.StringMaskAll",
    )
      .map(q => MaskedStrPathWithFunName(q._1, q._2))
      .toJsonPath()
      .right.get

    val value1 = maskData(jsObject, path)

    val value2 = value1.validate[UaspDto]


    val dto1 = dto.copy(
      id = "***MASKED***",
      dataString = Map("7" -> "***MASKED***"),
      process_timestamp = value2.get.process_timestamp)

    assertResult(dto1)(value2.get)

  }



}
