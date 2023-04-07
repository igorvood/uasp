package ru.vtb.uasp.inputconvertor.service

import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json.Json
import ru.vtb.uasp.inputconvertor.dao.CommonMsgAndProps.jsValueByType
import ru.vtb.uasp.inputconvertor.dao.dto.{SysDto, SysDtoParam}
import ru.vtb.uasp.inputconvertor.service.TransformHelper.splitMessage

class TransformHelperTest extends AnyFlatSpec {

  "split Success " should "ok" in {
    val value = jsValueByType("mdm")

    val seq = splitMessage[SysDtoParam](value, "contact")

    assert(seq.size == 4)

    assert(seq.map(q => q.get).contains(
      SysDtoParam(
        "10324",
        List(
          SysDto(
            "95000",
            "456",
            true
          )
        )
      )
    )
    )
  }


  "split ERR " should "ok" in {

    val sad =
      """{
        |  "messageResponse": {
        |    "contact": null
        |  }
        |}""".stripMargin

    val value = Json.parse(sad)

    val seq = splitMessage[SysDtoParam](value, "contact")
    assert(seq.size == 1)

    val value1 = seq.head.asEither.left.get.head._2.head.message

    assert(value1 == "Unable convert to array contact type JsNull")
  }
}
