package ru.vtb.uasp.mdm.enrichment.service

import play.api.libs.json.{Json, OWrites, Reads}

case class FlatJsonTestDto(
                            StringKey: String,
                            uniqueKey: Int,
                            global_id: String
                          )


object FlatJsonTestDto {


  implicit val reads: Reads[FlatJsonTestDto] = Json.reads[FlatJsonTestDto]
  implicit val write: OWrites[FlatJsonTestDto] = Json.writes[FlatJsonTestDto]
}