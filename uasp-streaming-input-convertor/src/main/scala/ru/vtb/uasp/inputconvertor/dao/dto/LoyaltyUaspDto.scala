package ru.vtb.uasp.inputconvertor.dao.dto

import play.api.libs.functional.syntax.{toFunctionalBuilderOps, toFunctorOps}
import play.api.libs.json.{Json, OWrites, Reads, __}
import ru.vtb.uasp.inputconvertor.dao.CommonDao.dtStringToLong

case class LoyaltyUaspDto(mdmId: String,
                          actualTime: Long,
                          loyaltyPrograms: List[LoyaltyProgram],
                          eventType: Option[String]
                         )

case class LoyaltyProgram(
                           options: List[OptionDto]
                         )

case class OptionDto(
                      code: String,
                      name: String,
                      startDate: String,
                      closeDate: Option[String],
                      createTime: String,

                    )

object OptionDto {
  implicit val reads: Reads[OptionDto] = Json.reads[OptionDto]
  implicit val writes: OWrites[OptionDto] = Json.writes[OptionDto]

}


object LoyaltyProgram {
  implicit val reads: Reads[LoyaltyProgram] = Json.reads[LoyaltyProgram]
  implicit val writes: OWrites[LoyaltyProgram] = Json.writes[LoyaltyProgram]

}


object LoyaltyUaspDto {
  implicit val reads: Reads[LoyaltyUaspDto] = (
    (__ \ "mdmId").read[String] and
      (__ \ "actualTime").read[String].fmap(q => dtStringToLong(q, "yyyy-MM-dd'T'HH:mm:ss", "GMT+0000")) and
      (__ \ "loyaltyPrograms").read[List[LoyaltyProgram]] and
      (__ \ "eventType").readNullable[String]
    ) (LoyaltyUaspDto.apply _)

  implicit val writes: OWrites[LoyaltyUaspDto] = Json.writes[LoyaltyUaspDto]

}
