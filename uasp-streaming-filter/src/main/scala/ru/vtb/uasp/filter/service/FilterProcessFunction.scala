package ru.vtb.uasp.filter.service

import org.apache.flink.streaming.api.scala.createTypeInformation
import ru.vtb.uasp.common.abstraction.DlqProcessFunction
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.service.JsonConvertOutService.IdentityPredef
import ru.vtb.uasp.common.service.dto.KafkaDto
import ru.vtb.uasp.filter.configuration.property.FilterRule
import ru.vtb.uasp.filter.service.FilterPredef.Predef

class FilterProcessFunction(private val filterConfig: FilterRule) extends DlqProcessFunction[UaspDto, UaspDto, KafkaDto]{

  override def processWithDlq(dto: UaspDto): Either[KafkaDto, UaspDto] =
    if (dto.filterResult(filterConfig)) {
      Right(dto)
    } else {
      Left(dto.serializeToBytes)
    }
}
