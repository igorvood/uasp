package ru.vtb.uasp.inputconvertor.service

import org.apache.flink.streaming.api.scala.createTypeInformation
import play.api.libs.json._
import ru.vtb.uasp.common.abstraction.DlqProcessFunctionOneToMany
import ru.vtb.uasp.common.service.dto.{OutDtoWithErrors, ServiceDataDto}
import ru.vtb.uasp.inputconvertor.constants.Config
import ru.vtb.uasp.inputconvertor.entity.InputMessageType
import ru.vtb.uasp.inputconvertor.factory.UaspDtoParser
import ru.vtb.uasp.inputconvertor.service.dto.JsValueAndKafkaKey
import ru.vtb.uasp.validate.DroolsValidator

import scala.util.{Failure, Success, Try}

class DtoConvertService(private val uaspDtoParser: UaspDtoParser,
                        private val droolsValidator: DroolsValidator,
                        private val serviceDataDto: ServiceDataDto,
                        private val dtoMap: Map[String, Array[String]]
                       ) extends DlqProcessFunctionOneToMany[InputMessageType, JsValueAndKafkaKey, OutDtoWithErrors[JsValue]] {
  override def processWithDlq(dto: InputMessageType): List[Either[OutDtoWithErrors[JsValue], JsValueAndKafkaKey]] = {
    val maybeJson = try {
      Right(Json.parse(new String(dto.message, Config.charset)))
    } catch {
      case e: Exception =>
        Left(OutDtoWithErrors[JsValue](serviceDataDto, Some(this.getClass.getName), List("Error json parsing: Input string is not Json"), None))
    }

    val resultList: List[Either[OutDtoWithErrors[JsValue], JsValueAndKafkaKey]] = maybeJson match {
      case Left(value) => List(Left(value))
      case Right(jsVal) => Try {
        uaspDtoParser.fromJValue(jsVal, dtoMap)
      } match {
        case Failure(exception) => List(Left(OutDtoWithErrors[JsValue](serviceDataDto, Some(this.getClass.getName), List(exception.getClass.getSimpleName + " -> " + exception.getMessage), Some(jsVal))))
        case Success(value) => value match {
          case Nil => List(Left[OutDtoWithErrors[JsValue], JsValueAndKafkaKey](OutDtoWithErrors[JsValue](serviceDataDto, Some(this.getClass.getName), List("list is nill"), Some(jsVal))))
          case list => list.map {
            case JsSuccess(value, path) =>
              val droolsErrors = droolsValidator.validate(List(value))
              val tupleOrKey: Either[OutDtoWithErrors[JsValue], JsValueAndKafkaKey] = if (droolsErrors.nonEmpty) {
                Left(
                  OutDtoWithErrors[JsValue](
                    serviceDataDto,
                    Some(this.getClass.getName),
                    List("Drools validation error: " + droolsErrors.map(_.msg).mkString("; ")),
                    Some(jsVal))
                )
              } else {
                Right(JsValueAndKafkaKey(dto.message_key, value))
              }
              tupleOrKey
            case JsError(errors) =>
              val e = errors.flatMap(q => {
                q._2.map(jsonValidationError => "JsPath error " + q._1 + "=>" + jsonValidationError.message).toList
              }).toList
              Left(
                OutDtoWithErrors[JsValue](
                  serviceDataDto,
                  Some(this.getClass.getName),
                  e
                  , Some(jsVal)))
          }
        }
      }
    }

    resultList

  }
}
