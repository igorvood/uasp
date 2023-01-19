package ru.vtb.uasp.vector.dsl.dto

import play.api.libs.json.{Json, OWrites, Reads}

case class Rule(name: String,
                description: String,
                mapping: Map[String, RuleField])

object Rule {
  implicit val uaspJsonReads: Reads[Rule] = Json.reads[Rule]
  implicit val uaspJsonWrites: OWrites[Rule] = Json.writes[Rule]
}
