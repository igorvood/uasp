package ru.vtb.uasp.vector.service

import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.scala.{OutputTag, createTypeInformation}
import org.apache.flink.util.Collector
import play.api.libs.json.Json
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.service.dto.KafkaDto
import ru.vtb.uasp.vector.dsl.{RuleHandler, RuleParser}
import ru.vtb.uasp.vector.service.RuleApplyMapFunction.dlqRuleApplyMapFunction

import java.util.UUID
import scala.util.{Failure, Success, Try}


class RuleApplyMapFunction extends KeyedProcessFunction[String, UaspDto, KafkaDto]{
  private val messageErrorFieldName = "messageError"

  override def processElement(value: UaspDto, context: KeyedProcessFunction[String, UaspDto, KafkaDto]#Context, collector: Collector[KafkaDto]): Unit = {
    Try {
      RuleHandler
        .applyAllRulesForUasp(value)
        .foreach(r => {
          r._2 match {
            case Left(e) => context.output(dlqRuleApplyMapFunction, KafkaDto(UUID.randomUUID().toString.getBytes(), Json.toJson(value.copy(dataString = value.dataString + (messageErrorFieldName -> e.getMessage)) ).toString().getBytes()) )
            case Right(value) =>
              val key = value.fields
                .filter(x => x._1.equals("MDM_ID"))
                .map(x=> x._2)
                .headOption.getOrElse(UUID.randomUUID().toString)
                .toString
                .replace("\"","").getBytes()

              context.output(RuleParser.tags(r._1.caseName), KafkaDto(key, value.toString().getBytes()))
          }
        })
    } match {
      case Failure(exception) => context.output(dlqRuleApplyMapFunction, KafkaDto(UUID.randomUUID().toString.getBytes(), Json.toJson(value.copy(dataString = value.dataString + (messageErrorFieldName -> exception.getMessage)) ).toString().getBytes()) )
      case Success(value) => value
    }
  }
}

object RuleApplyMapFunction {
  val dlqRuleApplyMapFunction: OutputTag[KafkaDto] = OutputTag[KafkaDto]("dlqRuleApplyMapFunction")
}
