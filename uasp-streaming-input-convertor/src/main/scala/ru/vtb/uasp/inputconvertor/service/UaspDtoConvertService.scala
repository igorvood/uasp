package ru.vtb.uasp.inputconvertor.service

import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.createTypeInformation
import org.apache.flink.util.Collector
import play.api.libs.json._
import ru.vtb.uasp.common.abstraction.DlqProcessFunction
import ru.vtb.uasp.common.service.dto.{OutDtoWithErrors, ServiceDataDto}
import ru.vtb.uasp.inputconvertor.constants.Config
import ru.vtb.uasp.inputconvertor.entity.InputMessageType
import ru.vtb.uasp.inputconvertor.factory.UaspDtoParser
import ru.vtb.uasp.inputconvertor.service.dto.UaspAndKafkaKey
import ru.vtb.uasp.validate.DroolsValidator

import scala.util.{Failure, Success, Try}

class UaspDtoConvertService(private val uaspDtoParser: UaspDtoParser,
                            private val droolsValidator: DroolsValidator,
                            private val serviceDataDto: ServiceDataDto,
                            private val dtoMap: Map[String, Array[String]]
                           ) extends DlqProcessFunction[InputMessageType, UaspAndKafkaKey, OutDtoWithErrors[JsValue]] {

  /*так конечно нельзя, надо поправить */
  override def processWithDlq(dto: InputMessageType): Either[OutDtoWithErrors[JsValue], UaspAndKafkaKey] = ???

  override def processElement(inputMessageType: InputMessageType, ctx: ProcessFunction[InputMessageType, UaspAndKafkaKey]#Context, out: Collector[UaspAndKafkaKey]): Unit = {


    val maybeJson = try {
      Right(Json.parse(new String(inputMessageType.message, Config.charset)))
    } catch {
      case e: Exception =>
        Left(OutDtoWithErrors[JsValue](serviceDataDto, Some(this.getClass.getName), List("Error json parsing: "
          + e.getMessage
          + ", with allProps: " + dtoMap.filterKeys(key => !key.contains("password"))), None))
    }

    maybeJson match {
      case Left(outErr) => ctx.output[OutDtoWithErrors[JsValue]](dlqOutPut, outErr)
      case Right(jsVal) => Try {
        uaspDtoParser.fromJValue(jsVal, dtoMap)
      } match {
        case Failure(exception) => ctx.output[OutDtoWithErrors[JsValue]](dlqOutPut, OutDtoWithErrors[JsValue](serviceDataDto, Some(this.getClass.getName), List(exception.getClass.getSimpleName + " -> " + exception.getMessage), Some(jsVal)))
        case Success(value) => value match {
          case Nil => ctx.output[OutDtoWithErrors[JsValue]](dlqOutPut, OutDtoWithErrors[JsValue](serviceDataDto, Some(this.getClass.getName), List("list is nill"), Some(jsVal)))
          case list => list.foreach {
            case JsSuccess(value, path) =>
              val droolsErrors = droolsValidator.validate(List(value))
              if (droolsErrors.nonEmpty) {
                ctx.output[OutDtoWithErrors[JsValue]](
                  dlqOutPut,
                  OutDtoWithErrors[JsValue](
                    serviceDataDto,
                    Some(this.getClass.getName),
                    List("Drools validation error: " + droolsErrors.map(_.msg).mkString("; ")),
                    Some(jsVal))
                )
              } else {
                out.collect(UaspAndKafkaKey(inputMessageType.message_key, value))
              }
            case JsError(errors) =>
              val e = errors.flatMap(q => {
                q._2.map(jsonValidationError => "JsPath error " + q._1 + "=>" + jsonValidationError.message).toList
              }).toList
              ctx.output[OutDtoWithErrors[JsValue]](dlqOutPut,
                OutDtoWithErrors[JsValue](
                  serviceDataDto,
                  Some(this.getClass.getName),
                  e
                  , Some(jsVal)))
          }
        }
      }


    }
  }
}
