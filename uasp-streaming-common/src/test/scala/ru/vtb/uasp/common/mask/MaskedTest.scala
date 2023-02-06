package ru.vtb.uasp.common.mask

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json.{JsObject, JsValue, Json}
import ru.vtb.uasp.common.dto.UaspDto



class MaskedTest extends AnyFlatSpec with should.Matchers {

//  def maskData(jsObject: JsValue, path: JPath): JsValue = {
//    path match {
//      case JPathObject(name, inner) =>
//    }
//
//    ???
//  }

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
    println(jsObject)

//    val path = List(
//      "id",
//      "dataString.7",
//    ).map(MaskedStrPath)
//      .toJsonPath()
//
//    maskData(jsObject, path)


  }



}
