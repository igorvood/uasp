package ru.vtb.uasp.common.mask.dto

import play.api.libs.json.{Json, OWrites, Reads}

case class JsMaskedPathError(error: String)

object JsMaskedPathError{
  implicit val uaspJsonReads: Reads[JsMaskedPathError] = Json.reads[JsMaskedPathError]
  implicit val uaspJsonWrites: OWrites[JsMaskedPathError] = Json.writes[JsMaskedPathError]

}