package ru.vtb.uasp.common.mask

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json.{JsValue, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.mask.MaskedPredef.{MaskJsValuePredef, PathFactory}

import java.util.Calendar

class PerfomanceMaskedTest extends AnyFlatSpec with should.Matchers {

  "mask all existing fields " should " OK" in {

    val dto = UaspDto(
      id = "1",
      dataInt = Map("2" -> 2),
      dataLong = Map("3" -> 3),
      dataFloat = Map("4" -> 4),
      dataDouble = Map("5" -> 5),
      dataDecimal = Map("6" -> 6),
      dataString = Map(
        "serName" -> "Салтыков-Щедрин",
        "name" -> "Михаил",
        "secondName" -> "Евграфович",
        "passportNum" -> "12 345678",
        "passportDepartment" -> "Отделом милиции №12 УВД №12 г. Петропавлоск-Камчатский",
        "phoneNum" -> "+7 965 123 45 67",
        "address" -> "Спас-Угол Калязинского уезда Тверской губернии дом 15"
      ),
      dataBoolean = Map("8" -> true),
      uuid = "uuid",
      process_timestamp = 18
    )

    val path = Map(
      "dataString.serName" -> "ru.vtb.uasp.common.mask.fun.NameMaskService",
      "dataString.name" -> "ru.vtb.uasp.common.mask.fun.NameMaskService",
      "dataString.secondName" -> "ru.vtb.uasp.common.mask.fun.NameMaskService",
      "dataString.passportNum" -> "ru.vtb.uasp.common.mask.fun.PassportNumberInStrMaskService",
      "dataString.passportDepartment" -> "ru.vtb.uasp.common.mask.fun.PassportDepartmentMaskService",
      "dataString.phoneNum" -> "ru.vtb.uasp.common.mask.fun.PhoneStrMaskService",
      "dataString.address" -> "ru.vtb.uasp.common.mask.fun.AddressMaskService"
    )
      .map(q => MaskedStrPathWithFunName(q._1, q._2))
      .toJsonPath()
      .right.get


    val dtoes = (1 to 100000).map(q =>
      dto.copy(id = q.toString)
    )
      .map(d => Json.toJsObject(d))


    val beginTime = Calendar.getInstance().getTime.getTime

    val errorsOrValues = dtoes.map(d => d.toMaskedJson(path).right.get)

    val endTime = Calendar.getInstance().getTime.getTime

    val duration = endTime - beginTime

    assert(duration < 10000)

    println(s"masked ${errorsOrValues.size}, duration $duration milliseconds")

  }


}
