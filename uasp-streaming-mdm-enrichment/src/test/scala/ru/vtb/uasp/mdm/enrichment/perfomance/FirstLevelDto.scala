package ru.vtb.uasp.mdm.enrichment.perfomance

import play.api.libs.json.{Json, OWrites, Reads}

case class FirstLevelDto(sl: SecondLevelDto = SecondLevelDto(),
                         OPERATION_ID: String,
                         MDM_ID: String,
                         EVENT_DTTM: String,
                         NEW_PACKAGE: String,
                         OLD_PACKAGE: String,
                         OLD_PACKAGE1: String = "4564654",
                         OLD_PACKAGE2: String = "4564654",
                         OLD_PACKAGE3: String = "4564654",
                         OLD_PACKAGE4: String = "4564654",
                         OLD_PACKAGE5: String = "4564654",
                         OLD_PACKAGE6: String = "4564654",
                         OLD_PACKAGE7: String = "4564654",
                         OLD_PACKAGE8: String = "4564654",
                         OLD_PACKAGE9: String = "4564654",
                         OLD_PACKAGE10: String = "4564654",
                        ) {
  require(NEW_PACKAGE != OLD_PACKAGE, s"OLD_PACKAGE must not equals NEW_PACKAGE. OLD_PACKAGE->$OLD_PACKAGE,  NEW_PACKAGE->$NEW_PACKAGE ")
}


object FirstLevelDto {

  implicit val reads: Reads[FirstLevelDto] = Json.reads[FirstLevelDto]
  implicit val write: OWrites[FirstLevelDto] = Json.writes[FirstLevelDto]

}
