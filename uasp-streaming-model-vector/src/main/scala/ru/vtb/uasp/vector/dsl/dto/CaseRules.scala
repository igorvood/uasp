package ru.vtb.uasp.vector.dsl.dto

import play.api.libs.json.{Json, OWrites, Reads}

case class CaseRules(caseName: String,
                     outputTopicName: String,
                     fields: Seq[Rule])

object CaseRules {
  implicit val uaspJsonReads: Reads[CaseRules] = Json.reads[CaseRules]
  implicit val uaspJsonWrites: OWrites[CaseRules] = Json.writes[CaseRules]
}
