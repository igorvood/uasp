package ru.vtb.uasp.common.utils.json

import play.api.libs.json.{Json, OWrites, Reads}

case class MetaData(lvl: Set[ModelVectorProperty])

object MetaData {
  implicit val writes: OWrites[MetaData] = Json.writes[MetaData]

  implicit val reads: Reads[MetaData] = Json.reads[MetaData]
}
