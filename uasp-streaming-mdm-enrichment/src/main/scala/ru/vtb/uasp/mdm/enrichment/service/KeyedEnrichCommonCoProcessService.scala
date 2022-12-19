package ru.vtb.uasp.mdm.enrichment.service

import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction
import org.apache.flink.util.Collector
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.mdm.enrichment.service.dto.{KeyedCAData, KeyedUasp}
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.CommonEnrichProperty

class KeyedEnrichCommonCoProcessService(
                                         val commonEnrichProperty: CommonEnrichProperty
                                       ) extends KeyedCoProcessFunction[String, KeyedUasp, KeyedCAData, Either[(UaspDto, String), UaspDto]] {

  val valueStateDescriptor = new ValueStateDescriptor(
    s"CommonState",
    TypeInformation.of(classOf[Map[String, String]])
  )
  private var dataState: ValueState[Map[String, String]] = _

  override def processElement1(value: KeyedUasp, ctx: KeyedCoProcessFunction[String, KeyedUasp, KeyedCAData, Either[(UaspDto, String), UaspDto]]#Context, out: Collector[Either[(UaspDto, String), UaspDto]]): Unit = {
    val mapState = Option(dataState.value()).getOrElse(Map.empty)

    val fieldsList = commonEnrichProperty.fields
    val mayBeError = value.enrichMainStream(fieldsList) { fieldKey => mapState.get(fieldKey) }

    val tupleOrUasp = mayBeError match {
      case Right(ok) => Right(ok.uaspDto)
      case Left(err) => Left(value.uaspDto, err)
    }

    out.collect(tupleOrUasp)
  }

  override def processElement2(value: KeyedCAData, ctx: KeyedCoProcessFunction[String, KeyedUasp, KeyedCAData, Either[(UaspDto, String), UaspDto]]#Context, out: Collector[Either[(UaspDto, String), UaspDto]]): Unit = {
    dataState.update(value.data)
  }

  override def open(config: Configuration): Unit = {
    dataState = getRuntimeContext.getState(valueStateDescriptor)
  }
}
