package ru.vtb.uasp.common.service.dto

import play.api.libs.functional.FunctionalBuilder
import play.api.libs.functional.syntax.{toFunctionalBuilderOps, unlift}
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{JsPath, OWrites, Reads}

case class OutDtoWithErrors[T](
                                   serviceDataDto: ServiceDataDto,
                                   errorPosition: Option[String],
                                   errors: List[String],
                                   data: Option[T],

                                 )


object OutDtoWithErrors {

  implicit def outDtoWithErrorsJsonReads[T](implicit fmt: Reads[T]): Reads[OutDtoWithErrors[T]] = json =>
    for {
      serviceDataDto <- (json \ "serviceDataDto").validate[ServiceDataDto]
      errorPosition <- (json \ "errorPosition").validateOpt[String]
      errors <- (json \ "errors").validate[List[String]]
      data <- (json \ "data").validateOpt[T](fmt)
    } yield new OutDtoWithErrors[T](serviceDataDto, errorPosition, errors, data)


  implicit def outDtoWithErrorsJsonWrites[T](implicit fmt: OWrites[T]): OWrites[OutDtoWithErrors[T]] = {
    val value: FunctionalBuilder[OWrites]#CanBuild4[ServiceDataDto, Option[String], List[String], Option[T]] =
      (JsPath \ "serviceDataDto").write[ServiceDataDto] and
        (JsPath \ "errorPosition").writeNullable[String] and
        (JsPath \ "errors").write[List[String]] and
        (JsPath \ "data").writeNullable[T](fmt)
    value(unlift(OutDtoWithErrors.unapply[T]))
  }


}