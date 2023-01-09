package ru.vtb.bevent.first.salary.aggregate.service

import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.state.{MapState, MapStateDescriptor}
import org.apache.flink.api.common.typeinfo.{TypeHint, TypeInformation}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.{KeyedProcessFunction, ProcessFunction}
import org.apache.flink.util.Collector
import ru.vtb.bevent.first.salary.aggregate.UaspStreamingAggregateFirstSalary.dlqOutputTag
import ru.vtb.bevent.first.salary.aggregate.constants.ConfirmedPropsModel
import ru.vtb.bevent.first.salary.aggregate.dao.AggregateDao
import ru.vtb.bevent.first.salary.aggregate.dao.AggregateDao.fullStateUpdate
import ru.vtb.bevent.first.salary.aggregate.entity.CountsAggregate
import ru.vtb.bevent.first.salary.aggregate.factory.{BusinessRules, BusinessRulesFactory}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.mutator.service.BusinessRulesService.errFieldName

class AggregateFirstSalaryRichMapFunction(props: ConfirmedPropsModel) extends KeyedProcessFunction[String,UaspDto,UaspDto] {

  private var salaryCountAggregateState: MapState[String, CountsAggregate] = _
  private val businessRules: BusinessRules = BusinessRulesFactory.getBusinessRules(props)


  override def processElement(inMsg: UaspDto, ctx: KeyedProcessFunction[String, UaspDto, UaspDto]#Context, out: Collector[UaspDto]): Unit = {
    val sourceClassifiedUasp = businessRules.level0.map(inMsg)
    if (sourceClassifiedUasp.dataString.contains(errFieldName)) {
      ctx.output(dlqOutputTag, sourceClassifiedUasp)
    } else {
      val (accountNumber: String, result: UaspDto) = AggregateDao.enrichState(salaryCountAggregateState, sourceClassifiedUasp)
      val uaspDtoProccessed = runBusinessRules(businessRules, result)
      if (sourceClassifiedUasp.dataString.contains(errFieldName)) {
        ctx.output(dlqOutputTag, sourceClassifiedUasp)
      } else {
        try {
          out.collect(uaspDtoProccessed)
          fullStateUpdate(salaryCountAggregateState, accountNumber, uaspDtoProccessed)
        } catch {
          case e: Exception =>
            val uaspWithErrorMessage = UaspDto(
              uaspDtoProccessed.id,
              Map.empty,
              Map.empty,
              Map.empty,
              Map.empty,
              Map.empty,
              Map("uaspDtoString" -> uaspDtoProccessed.toString, "errorMessage" -> e.getMessage),
              Map("isUaspDtoInString" -> true),
              uaspDtoProccessed.id,
              System.currentTimeMillis())

            ctx.output(dlqOutputTag, uaspWithErrorMessage)
        }
      }
    }

  }

  private def runBusinessRules(businessRules: BusinessRules, data: UaspDto): UaspDto = {
    val uaspDtoProccessedLevel1 = businessRules.level1.map(data) // BusinessRulesService(props.listOfBusinessRuleLevel1).map(data)
    val uaspDtoProccessedLevel2 = businessRules.level2.map(uaspDtoProccessedLevel1) // BusinessRulesService(props.listOfBusinessRuleLevel2).map(uaspDtoProccessedLevel1)
    val uaspDtoProccessed = businessRules.cases.map(uaspDtoProccessedLevel2) // BusinessRulesService(props.listOfBusinessRule).map(uaspDtoProccessedLevel2)
    uaspDtoProccessed
  }

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)

    val keyDayHourAggregatesTypeInfo = TypeInformation.of(new TypeHint[String]() {})
      .createSerializer(new ExecutionConfig())

    val valueDayHourAggregatesTypeInfo = TypeInformation.of(new TypeHint[CountsAggregate]() {})
      .createSerializer(new ExecutionConfig())

    salaryCountAggregateState = getRuntimeContext.getMapState(new MapStateDescriptor[String, CountsAggregate](
      props.nameStateFirstSalaryAggregates,
      keyDayHourAggregatesTypeInfo,
      valueDayHourAggregatesTypeInfo))
  }
}
