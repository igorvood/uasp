package ru.vtb.uasp.mdm.enrichment.dao

import play.api.libs.json.{JsPath, JsonValidationError}

object JsonPredef {


  implicit class PreDef(val self: Seq[(JsPath, Seq[JsonValidationError])]) extends AnyVal {

    def extractStringError(): String =
      self
        .map(err => "error by path " + (err._1 -> err._2.map(e => e.message).mkString(",")))
        .mkString("\n")
  }

}
