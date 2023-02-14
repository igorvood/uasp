package ru.vtb.uasp.mdm.enrichment.service

import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json.{JsValue, Json}
import ru.vtb.uasp.common.service.dto.{OutDtoWithErrors, ServiceDataDto}
import ru.vtb.uasp.mdm.enrichment.TestConst.globalIdStreamProperty
import ru.vtb.uasp.mdm.enrichment.service.KeyEnrichmentGlobalIdServiceTest._
import ru.vtb.uasp.mdm.enrichment.service.dto.KeyedCAData
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf.InputFormatEnum.FlatJsonFormat
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.{GlobalIdEnrichProperty, KeySelectorProp}

class ExtractKeyFunctionTest extends AnyFlatSpec {

  val stringKey = "StringKey"
  val someStrValue = "Какое-то значение"

  implicit private val serviceDataDto: ServiceDataDto = ServiceDataDto("йц", "ук", "ке")
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
    val outDtoWithErrors = OutDtoWithErrors[JsValue](
      serviceDataDto,
      Some("ru.vtb.uasp.mdm.enrichment.service.ExtractKeyFunction"),
      List("Not found new id for Uasp field name global_id field type String"),
      Some(Json.toJson(newMsg)) /*Json.stringify(jsValue), List("Unable to find key value Some(uniqueKey)")*/)

    runTest(
      globalIdStreamProp = globalIdStreamProperty,
      jsValue = jsValue,
      Left(outDtoWithErrors)
      //      expected = Left(OutDtoWithErrors(newMsg.serializeToStr, List("Not found new id for Uasp field name global_id field type String")))
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
    val value = Json.toJson(getMdmUaspDto)
    val outDtoWithErrors = OutDtoWithErrors[JsValue](
      serviceDataDto,
      Some("ru.vtb.uasp.mdm.enrichment.service.ExtractKeyFunction"),
      List("Unable to read key from field StringKey with type STRING"),
      Some(value) /*Json.stringify(jsValue), List("Unable to find key value Some(uniqueKey)")*/)


    runTest(
      globalIdStreamProp = globalIdStreamProperty.copy(keySelectorEnrich = KeySelectorProp(false, Some("STRING"), Some(stringKey))),
      jsValue = value,
      Left(outDtoWithErrors)
      //      expected = Left(OutDtoWithErrors(getMdmUaspDto.serializeToStr, List("Unable to read key from field StringKey with type STRING")))
    )

  }

  it should " validate is error on key null" in {
    val jsValue = Json.toJson(getMdmUaspDto.copy(id = null))
    val outDtoWithErrors = OutDtoWithErrors[JsValue](
      serviceDataDto,
      Some("ru.vtb.uasp.mdm.enrichment.service.ExtractKeyFunction"),
      List("Field 'id' in UaspDto is null, unable to use it on key"),
      Some(jsValue))
    runTest(
      globalIdStreamProp = globalIdStreamProperty,
      jsValue = jsValue,
      Left(outDtoWithErrors)
      //      expected = Left(OutDtoWithErrors(Json.stringify(jsValue), List("Field 'id' in UaspDto is null, unable to use it on key")))
    )
  }

  it should " validate is error wrong structure" in {

    val jsValue = Json.parse("{}")
    val outDtoWithErrors = OutDtoWithErrors[JsValue](
      serviceDataDto,
      Some("ru.vtb.uasp.mdm.enrichment.service.ExtractKeyFunction"),
      List("error by path (/uuid,error.path.missing)\nerror by path (/dataDecimal,error.path.missing)\nerror by path (/dataFloat,error.path.missing)\nerror by path (/dataLong,error.path.missing)\nerror by path (/process_timestamp,error.path.missing)\nerror by path (/id,error.path.missing)\nerror by path (/dataInt,error.path.missing)\nerror by path (/dataBoolean,error.path.missing)\nerror by path (/dataDouble,error.path.missing)\nerror by path (/dataString,error.path.missing)"),
      Some(jsValue))
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
    val outDtoWithErrors = OutDtoWithErrors[JsValue](
      serviceDataDto,
      Some("ru.vtb.uasp.mdm.enrichment.service.ExtractKeyFunction"),
      List("Value for key global_id is null, but value isn't optional"),
      Some(jsValue))
    runTest(
      globalIdStreamProp = globalIdEnrichPropertyFlatJsonFormat,
      jsValue = jsValue,
      Left(outDtoWithErrors)
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
    val outDtoWithErrors = OutDtoWithErrors[JsValue](
      serviceDataDto,
      Some("ru.vtb.uasp.mdm.enrichment.service.ExtractKeyFunction"),
      List("Unable to find key value Some(StringKey_ads)"),
      Some(jsValue))

    runTest(
      globalIdStreamProp = globalIdEnrichPropertyFlatJsonFormat.copy(keySelectorEnrich = KeySelectorProp(false, Some("STRING"), Some(stringKey + "_ads"))),
      jsValue = jsValue,
      Left(outDtoWithErrors)
    )

  }

  it should " validate is error wrong structure" in {

    val jsValue = Json.parse("{}")
    val outDtoWithErrors = OutDtoWithErrors[JsValue](
      serviceDataDto,
      Some("ru.vtb.uasp.mdm.enrichment.service.ExtractKeyFunction"),
      List("Unable to find key value Some(uniqueKey)"),
      Some(jsValue))

    runTest(
      globalIdStreamProp = globalIdEnrichPropertyFlatJsonFormat,
      jsValue = jsValue,
      expected = Left(outDtoWithErrors)
    )

  }


  private def runTest(globalIdStreamProp: GlobalIdEnrichProperty, jsValue: JsValue, expected: Either[OutDtoWithErrors[JsValue], KeyedCAData]) = {
    val enrichmentMapService = new ExtractKeyFunction(serviceDataDto, globalIdStreamProp)

    val dtoOrData: Either[OutDtoWithErrors[JsValue], KeyedCAData] = enrichmentMapService.processWithDlq(jsValue)

    expected match {
      case Right(value) => assertResult(Right(value))(dtoOrData)
      case Left(value) =>
        assert(dtoOrData.isLeft)
        val outDtoWithErrors = dtoOrData.left.get
        assertResult(Left(value))(Left(outDtoWithErrors))
    }


  }
}
