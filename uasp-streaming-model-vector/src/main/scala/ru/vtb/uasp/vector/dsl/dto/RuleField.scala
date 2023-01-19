package ru.vtb.uasp.vector.dsl.dto

import play.api.libs.json.{Json, OWrites, Reads}
import ru.vtb.uasp.vector.dsl.dto.MapType.MapType

case class RuleField(requirement: Boolean,
                     sourceName: String,
                     sourceType: MapType,
                     destinationName: String,
                     destinationType: MapType,
                     transformFunction: String)

object RuleField {
  implicit val uaspJsonReads: Reads[RuleField] = Json.reads[RuleField]
  implicit val uaspJsonWrites: OWrites[RuleField] = Json.writes[RuleField]
}
