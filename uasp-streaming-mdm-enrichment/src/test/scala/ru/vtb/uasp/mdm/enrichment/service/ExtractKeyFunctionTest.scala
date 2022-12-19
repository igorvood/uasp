package ru.vtb.uasp.mdm.enrichment.service

import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json.{JsValue, Json}
import ru.vtb.uasp.common.service.JsonConvertInService
import ru.vtb.uasp.common.service.JsonConvertOutService.JsonPredef
import ru.vtb.uasp.common.service.dto.OutDtoWithErrors
import ru.vtb.uasp.mdm.enrichment.TestConst.globalIdStreamProperty
import ru.vtb.uasp.mdm.enrichment.service.KeyEnrichmentGlobalIdServiceTest._
import ru.vtb.uasp.mdm.enrichment.service.dto.KeyedCAData
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf.InputFormatEnum.FlatJsonFormat
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.{GlobalIdEnrichProperty, KeySelectorProp}

import java.nio.charset.StandardCharsets

class ExtractKeyFunctionTest extends AnyFlatSpec {

  val stringKey = "StringKey"
  val someStrValue = "Какое-то значение"


  behavior of " validate UaspDto"


  it should " validate is ok" in {

    val newMsg = getMdmUaspDto.copy(dataString = getMdmUaspDto.dataString + (stringKey -> someStrValue))
    val jsValue = Json.toJson(newMsg)

    runTest(
      globalIdStreamProp = globalIdStreamProperty,
      jsValue = jsValue,
      expected = Right(KeyedCAData("LocalId1", Some("global_id_from_Mdm"), Map(stringKey -> someStrValue)))
    )

  }

  it should " validate is err, no id" in {

    val newMsg = getMdmUaspDto.copy(dataString = Map(stringKey -> someStrValue))
    val jsValue = Json.toJson(newMsg)

    runTest(
      globalIdStreamProp = globalIdStreamProperty,
      jsValue = jsValue,
      expected = Left(OutDtoWithErrors(newMsg.serializeToStr, List("Not found new id for Uasp field name global_id field type String")))
    )

  }


  it should " validate is ok optional value is empty" in {

    runTest(
      globalIdStreamProp = globalIdStreamProperty,
      jsValue = Json.toJson(getMdmUaspDto.copy(dataString = getMdmUaspDto.dataString - stringKey)),
      expected = Right(KeyedCAData("LocalId1", Some("global_id_from_Mdm"), Map()))
    )
  }

  it should " validate is ok on key from MAP" in {

    runTest(
      globalIdStreamProp = globalIdStreamProperty.copy(keySelectorEnrich = KeySelectorProp(false, Some("STRING"), Some(stringKey))),
      jsValue = Json.toJson(getMdmUaspDto.copy(dataString = getMdmUaspDto.dataString + (stringKey -> someStrValue))),
      expected = Right(KeyedCAData(someStrValue, Some("global_id_from_Mdm"), Map(stringKey -> someStrValue)))
    )

  }

  it should " validate is error on key from MAP" in {
    runTest(
      globalIdStreamProp = globalIdStreamProperty.copy(keySelectorEnrich = KeySelectorProp(false, Some("STRING"), Some(stringKey))),
      jsValue = Json.toJson(getMdmUaspDto),
      expected = Left(OutDtoWithErrors(getMdmUaspDto.serializeToStr, List("Unable to read key from field StringKey with type STRING")))
    )

  }

  it should " validate is error on key null" in {
    val jsValue = Json.toJson(getMdmUaspDto.copy(id = null))
    runTest(
      globalIdStreamProp = globalIdStreamProperty,
      jsValue = jsValue,
      expected = Left(OutDtoWithErrors(Json.stringify(jsValue), List("Field 'id' in UaspDto is null, unable to use it on key")))
    )
  }

  it should " validate is error wrong structure" in {

    val jsValue = Json.parse("{}")
    val outDtoWithErrors = OutDtoWithErrors(Json.stringify(jsValue), List("error by path (/uuid,error.path.missing)\nerror by path (/dataDecimal,error.path.missing)\nerror by path (/dataFloat,error.path.missing)\nerror by path (/dataLong,error.path.missing)\nerror by path (/process_timestamp,error.path.missing)\nerror by path (/id,error.path.missing)\nerror by path (/dataInt,error.path.missing)\nerror by path (/dataBoolean,error.path.missing)\nerror by path (/dataDouble,error.path.missing)\nerror by path (/dataString,error.path.missing)"))

    runTest(
      globalIdStreamProp = globalIdStreamProperty,
      jsValue = jsValue,
      expected = Left(outDtoWithErrors)
    )

  }

  behavior of " validate flatJson"

  private val flatJsonTestDto = FlatJsonTestDto(someStrValue, 141, "12")

  private val globalIdEnrichPropertyFlatJsonFormat: GlobalIdEnrichProperty = globalIdStreamProperty.copy(
    keySelectorEnrich = KeySelectorProp(false, Some("INT"), Some("uniqueKey")),
    inputDataFormat = FlatJsonFormat
  )

  it should " validate is ok" in {

    val jsValue = Json.toJson(flatJsonTestDto)

    runTest(
      globalIdStreamProp = globalIdEnrichPropertyFlatJsonFormat,
      jsValue = jsValue,
      expected = Right(KeyedCAData("141", Some("12"), Map(stringKey -> someStrValue)))
    )

  }

  it should " validate is err, no id" in {

    val flatJsonTestDtoNoID = flatJsonTestDto.copy(global_id = null)
    val jsValue = Json.toJson(flatJsonTestDtoNoID)

    runTest(
      globalIdStreamProp = globalIdEnrichPropertyFlatJsonFormat,
      jsValue = jsValue,
      expected = Left(OutDtoWithErrors(flatJsonTestDtoNoID.serializeToStr, List("Value for key global_id is null, but value isn't optional")))
    )

  }

  it should " validate is ok optional value is empty" in {

    val jsValue = Json.toJson(flatJsonTestDto.copy(StringKey = null))

    runTest(
      globalIdStreamProp = globalIdEnrichPropertyFlatJsonFormat,
      jsValue = jsValue,
      expected = Right(KeyedCAData("141", Some("12"), Map()))
    )
  }

  it should " validate is error on key from MAP" in {

    val jsValue = Json.toJson(flatJsonTestDto)
    runTest(
      globalIdStreamProp = globalIdEnrichPropertyFlatJsonFormat.copy(keySelectorEnrich = KeySelectorProp(false, Some("STRING"), Some(stringKey + "_ads"))),
      jsValue = jsValue,
      expected = Left(OutDtoWithErrors(flatJsonTestDto.serializeToStr, List("Unable to find key value Some(StringKey_ads)")))
    )

  }

  it should " validate is error wrong structure" in {

    val jsValue = Json.parse("{}")
    val outDtoWithErrors = OutDtoWithErrors(Json.stringify(jsValue), List("Unable to find key value Some(uniqueKey)"))

    runTest(
      globalIdStreamProp = globalIdEnrichPropertyFlatJsonFormat,
      jsValue = jsValue,
      expected = Left(outDtoWithErrors)
    )

  }


  private def runTest(globalIdStreamProp: GlobalIdEnrichProperty, jsValue: JsValue, expected: Either[OutDtoWithErrors, KeyedCAData]) = {
    val enrichmentMapService = new ExtractKeyFunction(globalIdStreamProp)

    val dtoOrData = enrichmentMapService.processWithDlq(jsValue)

    expected match {
      case Right(value) => assertResult(Right(value))(dtoOrData)
      case Left(value) =>
        assert(new String(dtoOrData.left.get.id, StandardCharsets.UTF_8).nonEmpty)
        val outDtoWithErrors = JsonConvertInService.deserialize[OutDtoWithErrors](dtoOrData.left.get.value).right.get
        assertResult(Left(value))(Left(outDtoWithErrors))
    }


  }
}
