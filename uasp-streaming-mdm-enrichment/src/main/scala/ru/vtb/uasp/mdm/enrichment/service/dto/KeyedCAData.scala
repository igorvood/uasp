package ru.vtb.uasp.mdm.enrichment.service.dto

case class KeyedCAData(key: String,
                       newId: Option[String],
                       data: Map[String, String],
//                       isDeleted: Boolean
                      )
