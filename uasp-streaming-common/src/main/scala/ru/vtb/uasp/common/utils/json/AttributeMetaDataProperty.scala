package ru.vtb.uasp.common.utils.json

import play.api.libs.json.{Json, OWrites, Reads}

case class AttributeMetaDataProperty(
                      dataType: String,
                      bLogic: String,
                      caFieldName: Option[String],
                      haFieldName: Option[String],
                      mvFieldName: String,
                      caseType: Option[String],
                      caFieldSupplementary: Option[String],
                      haFieldSupplementary: Option[String],
                      isAggregateFlg: Option[Boolean]
                    )

object AttributeMetaDataProperty {

  implicit val writes: OWrites[AttributeMetaDataProperty] = Json.writes[AttributeMetaDataProperty]

  implicit val reads: Reads[AttributeMetaDataProperty] = Json.reads[AttributeMetaDataProperty]
}
