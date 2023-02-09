package ru.vtb.uasp.common.abstraction

import play.api.libs.json.{Json, OWrites, Reads}

case class TestDataDto(srt: String,
                       num: Int,
                      )

object TestDataDto {

  implicit val uaspJsonReads: Reads[TestDataDto] = Json.reads[TestDataDto]
  implicit val uaspJsonWrites: OWrites[TestDataDto] = Json.writes[TestDataDto]

}