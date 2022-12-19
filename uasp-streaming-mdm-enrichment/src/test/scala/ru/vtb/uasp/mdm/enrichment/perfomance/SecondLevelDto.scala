package ru.vtb.uasp.mdm.enrichment.perfomance

import play.api.libs.json.{Json, OWrites, Reads}

case class SecondLevelDto(f1: Boolean = true,
                          f2: BigDecimal = 12,
                          f3: Int = 13,
                          f4: Long = 14,
                          f5: Float = 15,
                          f6: Double = 16,
                         )

object SecondLevelDto {

  implicit val reads: Reads[SecondLevelDto] = Json.reads[SecondLevelDto]
  implicit val write: OWrites[SecondLevelDto] = Json.writes[SecondLevelDto]

}
