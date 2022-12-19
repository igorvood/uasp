package ru.vtb.uasp.common.service

import play.api.libs.json._
import ru.vtb.uasp.common.service.JsonConvertInService.extractJsValue
import ru.vtb.uasp.common.service.dto.OutDtoWithErrors

import java.nio.charset.StandardCharsets
import scala.util.{Failure, Success, Try}

object JsonConvertInService extends Serializable {

  def deserialize[T](value: Array[Byte])(implicit reads: Reads[T]): Either[OutDtoWithErrors, T] = {
    val errorsOrT = for {
      extr <- extractJsValue(value)
      value1 = extr.validate[T]
      res <- value1 match {
        case JsSuccess(dto, _) => Right(dto)
        case JsError(errors) =>
          val errStr = errors
            .map(err => "error by path " + (err._1 -> err._2.map(e => e.message).mkString(",")))
            .mkString("\n")
          val jsonString = byteToStr(value)

          Left(OutDtoWithErrors(
            sourceValue = jsonString,
            errors = List(errStr)))
      }
    } yield res
    errorsOrT
  }


  private def byteToStr(value: Array[Byte]) = {
    Option(value).map(k => new String(k, StandardCharsets.UTF_8)).orNull
  }

  def extractJsValue(value: Array[Byte]): Either[OutDtoWithErrors, JsValue] = {
    val jsonString = byteToStr(value)
    val tryData = Try {
      Json.parse(jsonString)
    }
    val errorsOrDto = tryData match {
      case Success(d) => Right(d)
      case Failure(exception) => Left(OutDtoWithErrors(
        sourceValue = jsonString,
        errors = List(exception.getMessage)))

    }
    errorsOrDto

  }


}
