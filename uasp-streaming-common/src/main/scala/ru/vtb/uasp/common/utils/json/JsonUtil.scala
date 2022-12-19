package ru.vtb.uasp.common.utils.json

import play.api.libs.json.Json

import scala.collection.mutable
import scala.io.{BufferedSource, Source}

object JsonUtil {

  var casesMap: mutable.Map[String, mutable.Map[String, (String, String, String, String)]] = mutable.Map()

  @deprecated("use getFieldsForCasesDto")
  def getFieldsForCases(): Map[String, Map[String, Map[String, String]]] = {
    val jsonFields: String = jsonStr
    Json.parse(jsonFields).validate[Map[String, Map[String, Map[String, String]]]].get
  }

  def getFieldsForCasesDto(): MetaData = {
    val firstLevels = getFieldsForCases.map { fl =>
      val levels = fl._2.map { sl =>
        AtributeProperty(sl._1, AttributeMetaDataProperty(
          dataType = sl._2("type"),
          bLogic = sl._2("bLogic"),
          caFieldName = sl._2.get("caFieldName"),
          haFieldName = sl._2.get("haFieldName"),
          mvFieldName = sl._2("mvFieldName"),
          caseType = sl._2.get("caseType"),
          caFieldSupplementary = sl._2.get("caFieldSupplementary"),
          haFieldSupplementary = sl._2.get("haFieldSupplementary"),
          isAggregateFlg = sl._2.get("isAggregateFlg").map(_.toBoolean),
        ))
      }.toSet
      ModelVectorProperty(fl._1, levels)
    }.toSet

    MetaData(firstLevels)
  }

  private def jsonStr: String = {
    val jsonFieldsTmp: BufferedSource = Source.fromResource("ModelV5_All_Cases")

    val jsonFields: String = jsonFieldsTmp.getLines().mkString
    jsonFieldsTmp.close()
    jsonFields
  }

  @deprecated("use getAllCasesFields(jsonSchemaMap: MeatData)")
  def getAllCasesFields(jsonSchemaMap: Map[String, Map[String, Map[String, String]]]): Map[String, Map[String, String]] = {
    jsonSchemaMap.flatMap { v => v._2 }
  }

  def getAllCasesFields(jsonSchemaMap: MetaData): Set[AtributeProperty] = {
    jsonSchemaMap.lvl.flatMap { v => v.atributeProperties }
  }

}
