package ru.vtb.uasp.mdm.enrichment.service

import org.apache.flink.streaming.api.scala.createTypeInformation
import ru.vtb.uasp.common.abstraction.DlqProcessFunction
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.service.JsonConvertOutService.{IdentityPredef, JsonPredef}
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors}

@deprecated
class ThrowToDlqService extends DlqProcessFunction[Either[OutDtoWithErrors[UaspDto], UaspDto], UaspDto, OutDtoWithErrors[UaspDto]] {


//  override def processWithDlq(dto: Either[(UaspDto, String), UaspDto]): Either[KafkaDto, UaspDto] = {
//    dto match {
//      case Right(value) => Right(value)
//      case Left(value) => ???
//      //      case Left(value) => Left(OutDtoWithErrors(value._1.serializeToStr(None), List(value._2)).serializeToBytes)
//    }
//
//  }


//  override def processWithDlq(dto: Either[(UaspDto, String), UaspDto]): Either[OutDtoWithErrors[Either[OutDtoWithErrors[UaspDto], UaspDto]], UaspDto] = ???

  override def processWithDlq(dto: Either[OutDtoWithErrors[UaspDto], UaspDto]): Either[OutDtoWithErrors[UaspDto], UaspDto] = dto
}
