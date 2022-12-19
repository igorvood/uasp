package ru.vtb.uasp.common.utils.json

import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json.Json

class JsonUtilTest extends AnyFlatSpec {

  "data " should "be equals" in {
    val maps = JsonUtil.getFieldsForCases()
    val dtos = JsonUtil.getFieldsForCasesDto().lvl
      .map { fl =>
        fl.name -> fl.atributeProperties
          .map { sl =>
            "caFieldName" -> sl.property.caFieldName.toString
            "haFieldName" -> sl.property.haFieldName.toString
            "caseType" -> sl.property.caseType.toString

            val propMap = Map(
              "type" -> sl.property.dataType,
              "bLogic" -> sl.property.bLogic,
              "mvFieldName" -> sl.property.mvFieldName,
            )

            val propMap1 = sl.property.caFieldName
              .map { v => propMap + ("caFieldName" -> v) }
              .getOrElse(propMap)

            val propMap2 = sl.property.haFieldName
              .map { v => propMap1 + ("haFieldName" -> v) }
              .getOrElse(propMap1)

            val propMap3 = sl.property.caseType
              .map { v => propMap2 + ("caseType" -> v) }
              .getOrElse(propMap2)

            val propMap4 = sl.property.haFieldSupplementary
              .map { v => propMap3 + ("haFieldSupplementary" -> v) }
              .getOrElse(propMap3)

            val propMap5 = sl.property.caFieldSupplementary
              .map { v => propMap4 + ("caFieldSupplementary" -> v) }
              .getOrElse(propMap4)

            val propMap6 = sl.property.isAggregateFlg
              .map { v => propMap5 + ("isAggregateFlg" -> v) }
              .getOrElse(propMap5)

            sl.attributeName -> propMap5
          }
          .toMap
      }.toMap


    assertResult(0)((maps.keySet diff dtos.keySet).size)
    assertResult(0)((dtos.keySet diff maps.keySet).size)

    val innerMaps = maps.flatMap { q => q._2 }
    val innerDtos = dtos.flatMap { q => q._2 }

    assertResult(0)((innerMaps.keySet diff innerDtos.keySet).size)
    assertResult(0)((innerDtos.keySet diff innerMaps.keySet).size)

    val innerMapsStrings = innerMaps.map { q => q._1 + "->(" + q._2.map { w => w._1 + "=>" + w._2 }.toList.sorted.mkString(",") + ")" }.toList
    val innerDtosStrings = innerDtos.map { q => q._1 + "->(" + q._2.map { w => w._1 + "=>" + w._2 }.toList.sorted.mkString(",") + ")" }.toList


    val diff1 = innerMapsStrings diff innerDtosStrings
    val diff2 = innerDtosStrings diff innerMapsStrings
    assertResult(0)(diff1.size)

    assertResult(0)(diff2.size)
  }

  "data filter Test " should " be filtered" in {
    val maybeStrings = List(Some("1"), None)
    val strings = maybeStrings
      .filter(_.isDefined)
      .map {
        case Some(value) => value
      }

    val strings1 = maybeStrings.collect { case Some(value) => value }
    strings1

  }

  "data Optional Test " should " be filtered" in {
    //    val key = Some("2")
    val key = None
    val map = Map("2" -> "qwe")

    val maybeMaybeString = key
      .map(k => map.get(k))
      .collect { case Some(value) => value }


    println(maybeMaybeString)

  }

  "gen new jason " should " ok" in {
    val data = JsonUtil.getFieldsForCasesDto()
    val jsObject = Json.toJsObject(data)
    println(Json.stringify(jsObject))

  }
}
