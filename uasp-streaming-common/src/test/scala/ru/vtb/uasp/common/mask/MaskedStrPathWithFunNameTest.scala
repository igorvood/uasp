package ru.vtb.uasp.common.mask

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.mask.MaskedPredef.PathFactory
import ru.vtb.uasp.common.mask.dto.{JsMaskedPathError, JsMaskedPathObject, JsStringMaskedPathValue}
import ru.vtb.uasp.common.mask.fun.{CenterMaskService, StringMaskAll}

class MaskedStrPathWithFunNameTest extends AnyFlatSpec with should.Matchers {

  "no parameters masked function " should " OK " in {
    val jsMaskedPath = List(MaskedStrPathWithFunName("data.srt", "ru.vtb.uasp.common.mask.fun.StringMaskAll")).toJsonPath.right.get
    assert(JsMaskedPathObject(Map("data" -> JsMaskedPathObject(Map("srt" -> JsStringMaskedPathValue(StringMaskAll())))))==jsMaskedPath)

  }

  "no parameters masked function " should " error " in {
    val jsMaskedPath = List(MaskedStrPathWithFunName("data.srt", "ru.vtb.uasp.common.mask.fun.StringMaskAll1")).toJsonPath.left.get
    assert(List(JsMaskedPathError("unable to load class ru.vtb.uasp.common.mask.fun.StringMaskAll1 for data.srt. Cause ru.vtb.uasp.common.mask.fun.StringMaskAll1"))==jsMaskedPath)
  }


  "parameters masked function " should " OK " in {
    val jsMaskedPath = List(MaskedStrPathWithFunName("data.srt", "ru.vtb.uasp.common.mask.fun.CenterMaskService(2,3)")).toJsonPath.right.get
    assert(jsMaskedPath ==JsMaskedPathObject(Map("data" -> JsMaskedPathObject(Map("srt" -> JsStringMaskedPathValue(new CenterMaskService("2","3")))))))
  }

  "parameters masked function " should " error convert type " in {
    val jsMaskedPath = List(MaskedStrPathWithFunName("data.srt", "ru.vtb.uasp.common.mask.fun.CenterMaskService(2,3a)")).toJsonPath.left.get.head
    assert(jsMaskedPath ==JsMaskedPathError("unable to load class ru.vtb.uasp.common.mask.fun.CenterMaskService(2,3a) for data.srt. Cause null"))
  }

  "parameters masked function " should " error cnt of parameters, more " in {
    val jsMaskedPath = List(MaskedStrPathWithFunName("data.srt", "ru.vtb.uasp.common.mask.fun.CenterMaskService(2,3,4)")).toJsonPath.left.get.head
    assert(jsMaskedPath ==JsMaskedPathError("unable to load class ru.vtb.uasp.common.mask.fun.CenterMaskService(2,3,4) for data.srt. Cause wrong number of arguments"))
  }

  "parameters masked function " should " error cnt of parameters, less " in {
    val jsMaskedPath = List(MaskedStrPathWithFunName("data.srt", "ru.vtb.uasp.common.mask.fun.CenterMaskService(2)")).toJsonPath.left.get.head
    assert(jsMaskedPath ==JsMaskedPathError("unable to load class ru.vtb.uasp.common.mask.fun.CenterMaskService(2) for data.srt. Cause wrong number of arguments"))
  }

}
