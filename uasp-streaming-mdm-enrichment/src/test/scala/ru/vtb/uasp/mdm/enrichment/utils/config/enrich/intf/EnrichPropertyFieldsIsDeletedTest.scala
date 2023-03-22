package ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf

import org.scalatest.Assertion
import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json.{JsObject, Json, OWrites, Reads}
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf.EnrichPropertyFields.extractIsDeletedValue

import scala.collection.immutable
import scala.util.{Failure, Try}

class EnrichPropertyFieldsIsDeletedTest extends AnyFlatSpec {


  private val jsObject: JsObject = Json.toJsObject(WrapTestDto(SomeTestDto(None, true, 1, "1", List("q"))))


  val testCases: List[(String, Either[String, Boolean])] = List(
    "asd.asdasd" -> Right(false),
    "some.nullData" -> Right(false),
    "some.boolData" -> Right(true),

    "some.decimalData" -> Left("unable convert class play.api.libs.json.JsNumber to boolean"),
    "some.stringData" -> Left("unable convert class play.api.libs.json.JsString to boolean"),
    "some.arrayData" -> Left("unable convert class play.api.libs.json.JsArray to boolean"),
    "some" -> Left("unable convert class play.api.libs.json.JsObject to boolean"),

    "some.boolData.asdas" -> Left("unable convert class play.api.libs.json.JsTrue$ to boolean"),

    "some.decimalData.asdas" -> Left("unable convert class play.api.libs.json.JsNumber to boolean"),
    "some.stringData.asdas" -> Left("unable convert class play.api.libs.json.JsString to boolean"),
    "some.arrayData.asdas" -> Left("unable convert class play.api.libs.json.JsArray to boolean"),

  )


  it should " non empty path" in {
    val assertions: immutable.Seq[(String, Try[Assertion])] = testCases
      .map { testData =>
        val list = testData._1.split("\\.").toList
        val stringOrBoolean = extractIsDeletedValue(jsObject, list)
        testData._1 -> Try(
          stringOrBoolean match {
            case Left(value) => assert(value == testData._2.left.get)
            case Right(value) => assert(value == testData._2.right.get)
          }
        )
      }


    val errors = assertions
      .collect { case (str: String, triedAssertion: Failure[Assertion]) => s"for path $str error  ${triedAssertion.exception.getMessage} "
      }
    assert(errors.isEmpty)

    println(s"total testcases ${assertions.size}")

  }

  it should " empty path" in {
    assert(extractIsDeletedValue(jsObject, List.empty) == Right(false))
  }
}

case class WrapTestDto(
                        some: SomeTestDto,
                      )

case class SomeTestDto(
                        nullData: Option[String],
                        boolData: Boolean,
                        decimalData: Int,

                        stringData: String,
                        arrayData: List[String],
                      )

object WrapTestDto {
  implicit val uaspJsonReads: Reads[WrapTestDto] = Json.reads[WrapTestDto]
  implicit val uaspJsonWrites: OWrites[WrapTestDto] = Json.writes[WrapTestDto]

}

object SomeTestDto {
  implicit val uaspJsonReads: Reads[SomeTestDto] = Json.reads[SomeTestDto]
  implicit val uaspJsonWrites: OWrites[SomeTestDto] = Json.writes[SomeTestDto]

}

