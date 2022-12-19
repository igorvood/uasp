package ru.vtb.uasp.common.utils.json

import play.api.libs.json.{Json, OWrites, Reads}

case class AtributeProperty(attributeName: String,
                            property: AttributeMetaDataProperty

                       )

object AtributeProperty {
  implicit val writes: OWrites[AtributeProperty] = Json.writes[AtributeProperty]

  implicit val reads: Reads[AtributeProperty] = Json.reads[AtributeProperty]
}