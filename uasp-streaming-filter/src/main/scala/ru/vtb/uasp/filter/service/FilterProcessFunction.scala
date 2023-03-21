package ru.vtb.uasp.filter.service

import org.apache.flink.streaming.api.scala.createTypeInformation
import ru.vtb.uasp.common.abstraction.DlqProcessFunction
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.service.dto.{OutDtoWithErrors, ServiceDataDto}
import ru.vtb.uasp.filter.configuration.property.FilterRule
import ru.vtb.uasp.filter.service.FilterPredef.Predef

class FilterProcessFunction(private val filterConfig: FilterRule,
                            private val serviceDto: ServiceDataDto,
                           ) extends DlqProcessFunction[UaspDto, UaspDto, OutDtoWithErrors[UaspDto]] {


  private val className = Some(this.getClass.getName)

  private val filterRuleStr = List(filterConfig.toString)

  override def processWithDlq(dto: UaspDto): Either[OutDtoWithErrors[UaspDto], UaspDto] =
    if (dto.filterResult(filterConfig)) {
      Right(dto)
    } else {
      Left(OutDtoWithErrors[UaspDto](serviceDto, className, filterRuleStr, Some(dto)))
    }
}
