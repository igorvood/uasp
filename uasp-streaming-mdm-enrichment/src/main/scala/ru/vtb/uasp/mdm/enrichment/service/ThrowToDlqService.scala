package ru.vtb.uasp.mdm.enrichment.service

import org.apache.flink.streaming.api.scala.createTypeInformation
import ru.vtb.uasp.common.abstraction.DlqProcessFunction
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.service.JsonConvertOutService.JsonPredef
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors}

class ThrowToDlqService extends DlqProcessFunction[Either[(UaspDto, String), UaspDto], UaspDto, KafkaDto] {

  override def processWithDlq(dto: Either[(UaspDto, String), UaspDto]): Either[KafkaDto, UaspDto] = {
    dto match {
      case Right(value) => Right(value)
      case Left(value) => Left(OutDtoWithErrors(value._1.serializeToStr, List(value._2)).serializeToBytes)
    }

  }
}
