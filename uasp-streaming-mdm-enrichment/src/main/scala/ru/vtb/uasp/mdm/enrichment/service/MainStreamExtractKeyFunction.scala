package ru.vtb.uasp.mdm.enrichment.service

import org.apache.flink.streaming.api.scala.createTypeInformation
import ru.vtb.uasp.common.abstraction.DlqProcessFunction
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.service.JsonConvertOutService.JsonPredef
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors}
import ru.vtb.uasp.mdm.enrichment.service.dto.KeyedUasp
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf.EnrichPropertyFields

class MainStreamExtractKeyFunction(val mainStreamProperty: EnrichPropertyFields,
                                  ) extends DlqProcessFunction[UaspDto, KeyedUasp, OutDtoWithErrors[UaspDto]] {


//
//  override def processWithDlq(dto: UaspDto): Either[KafkaDto, KeyedUasp] = {
//    val mayBeError = mainStreamProperty.calcKey(
//      in = dto
//    )
//    mayBeError match {
//      case Right(value) => Right(value)
//      case Left(value) => ???
////      case Left(value) => Left(OutDtoWithErrors(dto.toString, List(value)).serializeToBytes(dto.id))
//
//    }
//  }

  override def processWithDlq(dto: UaspDto): Either[OutDtoWithErrors[UaspDto], KeyedUasp] = ???
}
