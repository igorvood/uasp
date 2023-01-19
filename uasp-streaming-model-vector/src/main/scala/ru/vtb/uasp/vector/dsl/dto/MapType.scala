package ru.vtb.uasp.vector.dsl.dto

import play.api.libs.json.{Format, Json}

object MapType extends Enumeration {
  type MapType = Value

  val ID: MapType.Value = Value("ID")
  val INTEGER: MapType.Value = Value("INTEGER")
  val LONG: MapType.Value = Value("LONG")
  val FLOAT: MapType.Value = Value("FLOAT")
  val DOUBLE: MapType.Value = Value("DOUBLE")
  val DECIMAL: MapType.Value = Value("DECIMAL")
  val STRING: MapType.Value = Value("STRING")
  val BOOLEAN: MapType.Value = Value("BOOLEAN")
  val EMPTY: MapType.Value = Value("")

  implicit val format: Format[MapType] = Json.formatEnum(this)
}
