package ru.vtb.uasp.filter.service

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.{DataStream, OutputTag}
import org.apache.flink.util.Collector
import ru.vtb.uasp.common.base.EnrichFlinkDataStream.EnrichFlinkDataStream
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.filter.configuration.property.FilterRule
import ru.vtb.uasp.filter.service.FilterPredef.Predef
import ru.vtb.uasp.filter.service.OutTag.{outputTagsErrsF, outputTagsF}

class FilterProcessFunction(private val filterConfig: FilterRule) extends ProcessFunction[UaspDto, UaspDto] {

  val outputTags: OutputTag[UaspDto] = outputTagsF(filterConfig.tagPrefix)
  val outputTagsErrs: OutputTag[UaspDto] = outputTagsErrsF(filterConfig.tagPrefix)

  override def processElement(value: UaspDto, ctx: ProcessFunction[UaspDto, UaspDto]#Context, out: Collector[UaspDto]): Unit = {
    val bool = value.filterResult(filterConfig)

    if (bool) {
      out.collect(value)
    } else {
      ctx.output(outputTagsErrs, value)
    }
  }

  def process(inStr: DataStream[UaspDto]): DataStream[UaspDto] = {
    inStr.process(processElement)
      .enrichName(s"${filterConfig.tagPrefix}-filter")
  }


}
