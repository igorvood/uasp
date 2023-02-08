package ru.vtb.uasp.common.service.dto

import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.Json
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import ru.vtb.uasp.common.service.dto.ServiceDataDto.serviceDataDtoJsonReads

class OutDtoWithErrorsTest extends AnyFlatSpec  {

  val serviceDataDto = ServiceDataDto("1", "2", "3")

  val ad = List(
    OutDtoWithErrors(serviceDataDto, Some("Asdasd"), List("qwerty"), Some(serviceDataDto)),
      OutDtoWithErrors[ServiceDataDto](serviceDataDto, None, List("qwerty"), None)

  )


  "mask all existing fields " should " OK" in {

    ad.foreach { d =>
      val jsObjectString = Json.stringify(Json.toJsObject(d))
      val value = Json.parse(jsObjectString)
      val value1 = Json.fromJson[OutDtoWithErrors[ServiceDataDto]](value).get

      assertResult(d)( value1)
      println(jsObjectString)
    }





  }

}
