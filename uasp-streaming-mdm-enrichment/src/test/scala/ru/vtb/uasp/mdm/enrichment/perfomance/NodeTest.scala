package ru.vtb.uasp.mdm.enrichment.perfomance

import org.scalatest.flatspec.AnyFlatSpec
import ru.vtb.uasp.mdm.enrichment.perfomance.NodeTest.metaJsonProperty
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.flat.json._

class NodeTest extends AnyFlatSpec {

  behavior of "Node read Test"

  it should " deserialization property " in {


    val actual = NodeJsonMeta(metaJsonProperty)

    val expected = ObjectJsonMeta(
      Map("sl" -> ObjectJsonMeta(
        Map("f1" -> FieldJsonMeta(BooleanMeta(), List("sl", "f1")),
          "f2" -> FieldJsonMeta(NumberMeta(), List("sl", "f2")),
          "f3" -> FieldJsonMeta(NumberMeta(), List("sl", "f3")),
          "f4" -> FieldJsonMeta(NumberMeta(), List("sl", "f4")),
          "f5" -> FieldJsonMeta(NumberMeta(), List("sl", "f5")),
          "f6" -> FieldJsonMeta(NumberMeta(), List("sl", "f6"))
        ),
        List("sl")
      ),
        "OPERATION_ID" -> FieldJsonMeta(StringMeta(), List("OPERATION_ID"))
      ),
      List()
    )

    assertResult(expected)(actual)
  }

}

object NodeTest {
  val metaJsonProperty = Map("OPERATION_ID" -> "STRING",
    "sl.f1" -> "BOOLEAN",
    "sl.f2" -> "BIGDECIMAL",
    "sl.f3" -> "INT",
    "sl.f4" -> "LONG",
    "sl.f5" -> "FLOAT",
    "sl.f6" -> "DOUBLE",
  )
}
