package ru.vtb.uasp.inputconvertor.dao.dto

import play.api.libs.json.{Json, OWrites, Reads}


case class SysDtoParam(partyUId: String,
                       cifcontactReference: List[SysDto]
                      )


case class SysDto(systemNumber: String,
                  externalId: String,
                  is_deleted: String,
                 )


object SysDtoParam {

  implicit val uaspJsonReads: Reads[SysDtoParam] = Json.reads[SysDtoParam]
  implicit val uaspJsonWrites: OWrites[SysDtoParam] = Json.writes[SysDtoParam]

}


object SysDto {

  implicit val uaspJsonReads: Reads[SysDto] = Json.reads[SysDto]
  implicit val uaspJsonWrites: OWrites[SysDto] = Json.writes[SysDto]

}

