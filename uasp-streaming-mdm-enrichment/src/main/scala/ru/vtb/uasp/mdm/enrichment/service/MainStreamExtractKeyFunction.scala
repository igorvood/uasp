package ru.vtb.uasp.mdm.enrichment.service

import org.apache.flink.streaming.api.scala.createTypeInformation
import ru.vtb.uasp.common.abstraction.DlqProcessFunction
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.service.dto.{OutDtoWithErrors, ServiceDataDto}
import ru.vtb.uasp.mdm.enrichment.service.dto.KeyedUasp
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf.EnrichPropertyFields

class MainStreamExtractKeyFunction(serviceDataDto: ServiceDataDto,
                                   val mainStreamProperty: EnrichPropertyFields,
                                  ) extends DlqProcessFunction[UaspDto, KeyedUasp, OutDtoWithErrors[UaspDto]] {

  override def processWithDlq(dto: UaspDto): Either[OutDtoWithErrors[UaspDto], KeyedUasp] = {
    val mayBeError = mainStreamProperty.calcKey(
      in = dto
    )
    mayBeError match {
      case Left(value) => Left(OutDtoWithErrors(serviceDataDto, Some(this.getClass.getName), List(value), Some(dto)))
      case Right(value) => Right(value)
    }
  }
}
