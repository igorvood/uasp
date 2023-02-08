package ru.vtb.uasp.common.service

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.service.JsonConvert.{jsonStr, uasp_dto}
import ru.vtb.uasp.common.service.dto.{NewOutDtoWithErrors, ServiceDataDto}

class JsonConvert extends AnyFlatSpec with should.Matchers {

  implicit val serviceDataDto = ServiceDataDto("asd", "asd", "asd")
  behavior of "JsonConvert"



  it should "be serialized without modification" in {
    val dtoStr = JsonConvertOutService.serializeToStr(uasp_dto, None).right.get

    assertResult("1")(dtoStr.id)

    assertResult(jsonStr)(dtoStr.value)
  }

  it should "be deserialized without modification" in {
    val dtoBytes = JsonConvertOutService.serializeToBytes(uasp_dto, None).right.get


    val newUasp = JsonConvertInService.deserialize[UaspDto](dtoBytes.value)

    assertResult(uasp_dto)(newUasp.right.get)
  }

  it should "be deserialized with list err" in {
    val newUasp = JsonConvertInService.deserialize[UaspDto]("dtoBytes.value".getBytes())

    val value1 = newUasp.left.get
    assertResult(NewOutDtoWithErrors(serviceDataDto,
      Some("ru.vtb.uasp.common.service.JsonConvertInService$"),
      List("Unrecognized token 'dtoBytes': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')\n at [Source: (String)\"dtoBytes.value\"; line: 1, column: 9]"),
      None
    )
    )(value1)
  }


}


object JsonConvert {

  val jsonStr = "{\"id\":\"1\",\"dataInt\":{\"test1\":1},\"dataLong\":{\"test2\":2},\"dataFloat\":{\"test3\":3},\"dataDouble\":{\"test4\":4},\"dataDecimal\":{\"test7\":7},\"dataString\":{\"test5\":\"test\"},\"dataBoolean\":{\"test6\":false},\"uuid\":\"toString\",\"process_timestamp\":45}"

  val uasp_dto: UaspDto = UaspDto("1",
    Map[String, Int]("test1" -> 1),
    Map[String, Long]("test2" -> 2L),
    Map[String, Float]("test3" -> 3.0f),
    Map[String, Double]("test4" -> 4.0),
    Map[String, BigDecimal]("test7" -> BigDecimal(7.0)),
    Map[String, String]("test5" -> "test"),
    Map[String, Boolean]("test6" -> false),
    "toString",
    45)

}