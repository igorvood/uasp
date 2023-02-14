package ru.vtb.uasp.mdm.enrichment.service

import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction
import org.apache.flink.util.Collector
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.service.dto.{OutDtoWithErrors, ServiceDataDto}
import ru.vtb.uasp.mdm.enrichment.dao.UaspDtoPredef.PreDef
import ru.vtb.uasp.mdm.enrichment.service.dto.{KeyedCAData, KeyedUasp}
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.GlobalIdEnrichProperty

class KeyGlobalIdEnrichmentMapService(val serviceDataDto: ServiceDataDto,
                                      val globalIdStreamProperty: GlobalIdEnrichProperty,
                                      val appSavepointPref: String
                                     ) extends KeyedCoProcessFunction[String, KeyedUasp, KeyedCAData, Either[OutDtoWithErrors[UaspDto], UaspDto]] {

  val crossLinkMdmDataStateDescriptor: ValueStateDescriptor[String] =
    new ValueStateDescriptor(s"crossLinkMdmDataState_$appSavepointPref", TypeInformation.of(classOf[String]))

  val mapStateDescriptor = new ValueStateDescriptor(
    s"${appSavepointPref}_EnrichWithGlobalState",
    TypeInformation.of(classOf[Map[String, String]])
  )

  private var globalIdState: ValueState[String] = _

  private var dataState: ValueState[Map[String, String]] = _

  override def processElement1(value: KeyedUasp, ctx: KeyedCoProcessFunction[String, KeyedUasp, KeyedCAData, Either[OutDtoWithErrors[UaspDto], UaspDto]]#Context, out: Collector[Either[OutDtoWithErrors[UaspDto], UaspDto]]): Unit = {
    val maybeEnrichment: Either[String, KeyedUasp] = for {
      withId <- Option(globalIdState.value())
        .map { global_id => Right(value.copy(localId = global_id, uaspDto = value.uaspDto.enrichGlobalId(global_id, globalIdStreamProperty.globalEnrichFields))) }
        .getOrElse(Left("Not found global id in state for id = " + value.uaspDto.id))

      enrichmentUasp <- withId.enrichMainStream(globalIdStreamProperty.fields) { fieldKey => Option(dataState.value()).getOrElse(Map.empty).get(fieldKey) }

    } yield enrichmentUasp

    val maybeOk = maybeEnrichment match {
      case Right(ok) => Right(ok.uaspDto)
      case Left(err) => Left(OutDtoWithErrors[UaspDto](serviceDataDto, Some(this.getClass.getName), List(err), Some(value.uaspDto)))
    }

    out.collect(maybeOk)
  }

  override def processElement2(value: KeyedCAData, ctx: KeyedCoProcessFunction[String, KeyedUasp, KeyedCAData, Either[OutDtoWithErrors[UaspDto], UaspDto]]#Context, out: Collector[Either[OutDtoWithErrors[UaspDto], UaspDto]]): Unit = {

    value.newId
      .map { globalId => globalIdState.update(globalId) }
      .getOrElse {
        throw new IllegalStateException(" не возможная ситуация, до этого провалидировали все ID")
      }

    dataState.update(value.data)
  }

  override def open(parameters: Configuration): Unit = {
    globalIdState = getRuntimeContext.getState(crossLinkMdmDataStateDescriptor)
    dataState = getRuntimeContext.getState(mapStateDescriptor)

  }
}
