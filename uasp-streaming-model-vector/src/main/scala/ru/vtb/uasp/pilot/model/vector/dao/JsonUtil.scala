package ru.vtb.uasp.pilot.model.vector.dao

import play.api.libs.json.Json

import scala.io.{BufferedSource, Source}

object JsonUtil {

  def getFieldsForCases(): Map[String, Map[String, Map[String, String]]] = {
    getFieldsForCases("ModelV5_All_Cases")
  }

  def getFieldsForCases(resourceName: String): Map[String, Map[String, Map[String, String]]] = {
    val jsonFieldsTmp: BufferedSource = Source.fromResource(resourceName)
    val jsonFields: String = jsonFieldsTmp.getLines().mkString
    jsonFieldsTmp.close()

    Json.parse(jsonFields).validate[Map[String, Map[String, Map[String, String]]]].get
  }

}
