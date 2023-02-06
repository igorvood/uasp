package ru.vtb.uasp.common.mask

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import ru.vtb.uasp.common
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.mask.NewJPath.PathFactory



class MaskedTest extends AnyFlatSpec with should.Matchers {


  def maskData(jsObject: JsValue, path: NewJPath): JsValue = {
    val value1: JsValue = (jsObject, path) match {
      case (JsObject(values), NewJPathObject(masked)) => {

        val units = values
          .map(vv => {


            masked
              .get(vv._1)
              .map(q => vv._1 -> maskData(vv._2, q))
              .getOrElse(vv)
          }
          )
        JsObject(units)
      }
      case (value, NewJPathValue(masked)) => {
        val string = value match {
          case JsString(value) => JsString("masked " + value)
          case _ => throw new IllegalArgumentException("bad")

        }
        string
      }
      case (value, _) => throw new IllegalArgumentException("bad 2")
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

    val path = List(
      "id",
      "dataString.7",
    ).map(MaskedStrPath)
      .toJsonPath18()

//    println(path)
//
    val value1 = maskData(jsObject, path)
    println(value1)

    val value2 = value1.validate[UaspDto]
    println(dto)
    println(value2.get)


  }



}
