package ru.vtb.uasp.common.abstraction

import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{Json, OWrites, Reads}
import ru.vtb.uasp.common.mask.MaskedPredef.{MaskJsValuePredef, PathFactory}
import ru.vtb.uasp.common.mask.MaskedStrPathWithFunName
import ru.vtb.uasp.common.mask.fun._
import ru.vtb.uasp.common.test.MiniPipeLineTrait

class MaskTest extends AnyFlatSpec with MiniPipeLineTrait with Serializable {


  "mask with param " should " OK" in {
    val name = StringMaskAll.getClass.getName.replace("$", "")
    val path = List(
      MaskedStrPathWithFunName("str", name),
      MaskedStrPathWithFunName("num", name)
    ).toJsonPath.right.get

    val value = Json.toJson(MaskedTestDto("1234567890", 1234567890)).toMaskedJson(path).right.get

    assert("""{"str":"***MASKED***","num":"***MASKED***"}""" == Json.stringify(value))
  }

  "create all masked function " should " OK" in {

    val errorsOrPaths = List(AccountMask.getClass.getName.replace("$", ""),

      AddressMaskService.getClass.getName.replace("$", ""),
      BooleanMaskAll.getClass.getName.replace("$", ""),
      CenterMaskService.getClass.getName.replace("$", "") + "(1,1)",
      EMailMask.getClass.getName.replace("$", ""),
      FloatLengthCommonMaskService.getClass.getName.replace("$", ""),
      NameMaskService.getClass.getName.replace("$", ""),
      NumberMaskAll.getClass.getName.replace("$", ""),
      PassportDepartmentMaskService.getClass.getName.replace("$", ""),
      PassportNumberInStrMaskService.getClass.getName.replace("$", ""),
      PhoneStrMaskService.getClass.getName.replace("$", ""),
      StringMaskAll.getClass.getName.replace("$", ""),
    ).map { f => List(MaskedStrPathWithFunName("str", f)).toJsonPath }

    val list = errorsOrPaths
      .collect { case Left(value) => value }

    assert(list.isEmpty)

  }

}

case class MaskedTestDto(
                          str: String,
                          num: Double
                        )

object MaskedTestDto {
  implicit val uaspJsonReads: Reads[MaskedTestDto] = Json.reads[MaskedTestDto]
  implicit val uaspJsonWrites: OWrites[MaskedTestDto] = Json.writes[MaskedTestDto]

}

