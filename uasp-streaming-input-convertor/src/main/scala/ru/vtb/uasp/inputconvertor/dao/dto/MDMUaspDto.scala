package ru.vtb.uasp.inputconvertor.dao.dto

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json._


case class SysDtoParam(partyUId: String,
                       cifcontactReference: List[SysDto]
                      )


case class SysDto(systemNumber: String,
                  externalId: String,
                  is_deleted: Boolean,
                 )

object SysDtoParam {

  implicit val uaspJsonReads: Reads[SysDtoParam] = Json.reads[SysDtoParam]
  implicit val uaspJsonWrites: OWrites[SysDtoParam] = Json.writes[SysDtoParam]

}

object SysDto {

  implicit val reads1: Reads[SysDto] =
    (
      (__ \ "systemNumber").read[String] and
        (__ \ "externalId").read[String] and
        (__ \ "is_deleted").read[String] //(Reads.min("0") keepAnd Reads.max("1"))
          .flatMapResult {

            case "1" => JsSuccess(true)
            case "0" => JsSuccess(false)
            case er => JsError(s"is_deleted must be 0 or 1, current value is $er")

          }
      ) (SysDto.apply _)


  implicit val uaspJsonWrites: OWrites[SysDto] = Json.writes[SysDto]

}

