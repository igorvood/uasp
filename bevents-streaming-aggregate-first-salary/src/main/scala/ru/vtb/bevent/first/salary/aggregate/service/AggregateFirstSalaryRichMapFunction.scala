package ru.vtb.bevent.first.salary.aggregate.service

import org.apache.flink.api.common.ExecutionConfig
import org.apache.flink.api.common.state.{MapState, MapStateDescriptor}
import org.apache.flink.api.common.typeinfo.{TypeHint, TypeInformation}
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.util.Collector
import ru.vtb.bevent.first.salary.aggregate.UaspStreamingAggregateFirstSalary.dlqOutputTag
import ru.vtb.bevent.first.salary.aggregate.dao.AggregateDao
import ru.vtb.bevent.first.salary.aggregate.dao.AggregateDao.fullStateUpdate
import ru.vtb.bevent.first.salary.aggregate.entity.CountsAggregate
import ru.vtb.bevent.first.salary.aggregate.factory.BusinessRules
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.service.JsonConvertInService
import ru.vtb.uasp.common.service.JsonConvertOutService.IdentityPredef
import ru.vtb.uasp.common.service.dto.KafkaDto
import ru.vtb.uasp.mutator.service.BusinessRulesService.errFieldName

import scala.util.{Failure, Success, Try}

class AggregateFirstSalaryRichMapFunction(
                                           businessRules: BusinessRules,
                                           nameStateFirstSalaryAggregates: String
                                         ) extends KeyedProcessFunction[String, UaspDto, UaspDto] {

  private var salaryCountAggregateState: MapState[String, CountsAggregate] = _

  override def processElement(inMsg: UaspDto, ctx: KeyedProcessFunction[String, UaspDto, UaspDto]#Context, out: Collector[UaspDto]): Unit = {

    val either: Either[KafkaDto, UaspDto] = businessRules.level0.processWithDlq(inMsg)
      .flatMap(sourceClassifiedUasp =>
        if (sourceClassifiedUasp.dataString.contains(errFieldName)) {
          Left(sourceClassifiedUasp.serializeToBytes)
        } else Right(sourceClassifiedUasp)
      )
      .map(sourceClassifiedUasp => AggregateDao.enrichState(salaryCountAggregateState, sourceClassifiedUasp))
      .flatMap(result => runBusinessRules(businessRules, result._2).map(r => result._1 -> r))
      .flatMap(r => {
        Try {
          fullStateUpdate(salaryCountAggregateState, r._1, r._2)
          r._2
        } match {
          case Success(value) => Right(value)
          case Failure(e) => Left(UaspDto(
            r._2.id,
            Map.empty,
            Map.empty,
            Map.empty,
            Map.empty,
            Map.empty,
            Map("uaspDtoString" -> r._2.toString, "errorMessage" -> e.getMessage),
            Map("isUaspDtoInString" -> true),
            r._2.uuid,
            System.currentTimeMillis()).serializeToBytes)
        }
      }
      )

    either
    match {
      case Right(uaspDtoProccessed) => out.collect(uaspDtoProccessed)
      case Left(uaspDtoProccessed) =>
        JsonConvertInService.deserialize[UaspDto](uaspDtoProccessed.value)
          .map(ctx.output(dlqOutputTag, _))
          .getOrElse(throw new IllegalArgumentException("Сюда дойти никогда не должно " + uaspDtoProccessed))
    }

  }

  private def runBusinessRules(businessRules: BusinessRules, data: UaspDto): Either[KafkaDto, UaspDto] = {
    businessRules.level1.processWithDlq(data)
      .flatMap(u => businessRules.level2.processWithDlq(u))
      .flatMap(u => businessRules.cases.processWithDlq(u))
  }

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)

    val keyDayHourAggregatesTypeInfo = TypeInformation.of(new TypeHint[String]() {})
      .createSerializer(new ExecutionConfig())

    val valueDayHourAggregatesTypeInfo = TypeInformation.of(new TypeHint[CountsAggregate]() {})
      .createSerializer(new ExecutionConfig())

    salaryCountAggregateState = getRuntimeContext.getMapState(new MapStateDescriptor[String, CountsAggregate](
      nameStateFirstSalaryAggregates,
      keyDayHourAggregatesTypeInfo,
      valueDayHourAggregatesTypeInfo))
  }
}
