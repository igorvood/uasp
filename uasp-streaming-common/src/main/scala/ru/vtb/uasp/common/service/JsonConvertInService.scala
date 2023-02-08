package ru.vtb.uasp.common.service

import play.api.libs.json._
import ru.vtb.uasp.common.service.dto.{OutDtoWithErrors, ServiceDataDto}

import java.nio.charset.StandardCharsets
import scala.util.{Failure, Success, Try}

object JsonConvertInService extends Serializable {

  def deserialize[T](value: Array[Byte])(implicit reads: Reads[T], serviceDataDto: ServiceDataDto): Either[OutDtoWithErrors[T], T] = {
    val either: scala.util.Either[OutDtoWithErrors[T], T] = for {
      extr <- extractJsValue[T](value)
      value1 = extr.validate[T]
      res <- value1 match {
        case JsSuccess(dto, _) => Right(dto)
        case JsError(errors) => {
          val errStr = errors
            .map(err => "error by path " + (err._1 -> err._2.map(e => e.message).mkString(",")))
            .mkString("\n")
          Left(OutDtoWithErrors[T](
            serviceDataDto,
            Some(this.getClass.getName),
            List(errStr),
            None,
          )
          )
        }
      }
    } yield res
    either
  }


  private def byteToStr(value: Array[Byte]) = {
    Option(value).map(k => new String(k, StandardCharsets.UTF_8)).orNull
  }

  def extractJsValue[T](value: Array[Byte])(implicit serviceDataDto: ServiceDataDto): Either[OutDtoWithErrors[T], JsValue] = {
    val jsonString = byteToStr(value)
    val tryData = Try {
      Json.parse(jsonString)
    }
    val errorsOrDto = tryData match {
      case Success(d) => Right(d)
      case Failure(exception) => Left(
        OutDtoWithErrors[T](
          serviceDataDto,
          Some(this.getClass.getName),
          List(exception.getMessage),
          None,
        )
      )
    }
    errorsOrDto

  }


}
