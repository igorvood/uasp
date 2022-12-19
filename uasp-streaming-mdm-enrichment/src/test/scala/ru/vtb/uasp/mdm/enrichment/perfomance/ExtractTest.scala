package ru.vtb.uasp.mdm.enrichment.perfomance

import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json._
import ru.vtb.uasp.mdm.enrichment.perfomance.ExtractPerformanceTest.{inJsonStrValid, packageServiceInDto}
import ru.vtb.uasp.mdm.enrichment.perfomance.NodeTest.metaJsonProperty
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.flat.json._

import scala.util.{Failure, Try}


class ExtractTest extends AnyFlatSpec {

  behavior of "extractSimpleValue"

  it should " String  " in {
    val res = FieldJsonMeta(StringMeta(), List("root", "f1"))
      .extract(JsString("strValue"))

    assertResult(true)(res.isRight)
    assertResult(1)(res.right.get.size)
    assertResult("root.f1" -> "strValue")(res.right.get.toList.head)
  }

  it should " Number  " in {
    val res = FieldJsonMeta(NumberMeta(), List("root", "f1"))
      .extract(JsNumber(12))

    assertResult(true)(res.isRight)
    assertResult(1)(res.right.get.size)
    assertResult("root.f1" -> "12")(res.right.get.toList.head)
  }

  it should " Boolean  " in {
    val res = FieldJsonMeta(BooleanMeta(), List("root", "f1"))
      .extract(JsBoolean(true))
    assertResult(true)(res.isRight)
    assertResult(1)(res.right.get.size)
    assertResult("root.f1" -> "true")(res.right.get.toList.head)
  }

  it should " Null  " in {
    val res = FieldJsonMeta(BooleanMeta(), List("root", "f1"))
      .extract(JsNull)
    assertResult(true)(res.isRight)
    assertResult(0)(res.right.get.size)
  }


  it should " Non compatible data type then exception  " in {
    val tryVal =
      FieldJsonMeta(BooleanMeta(), List("root", "f1"))
        .extract(JsNumber(15))

    assertResult(false)(tryVal.isRight)
    assertResult("Non compatible data type for path root.f1 json val => 15, meta data type => BooleanMeta()")(tryVal.left.get)
  }

  it should " extract obj " in {
    val json = inJsonStrValid(packageServiceInDto)

    val jsValue = Json.parse(json)

    val meta = NodeJsonMeta(metaJsonProperty)

    val resMap = meta.extract(jsValue)

    assertResult(Map(
      "sl.f1" -> "true",
      "sl.f2" -> "12",
      "sl.f3" -> "13",
      "sl.f4" -> "14",
      "sl.f5" -> "15",
      "sl.f6" -> "16",
      "OPERATION_ID" -> "1")
    )(resMap.right.get)

  }

}


