package ru.vtb.uasp.common.service

import org.apache.flink.api.scala.createTypeInformation
import ru.vtb.uasp.common.abstraction.DlqProcessFunction
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.mask.dto.JsMaskedPathError
import ru.vtb.uasp.common.service.JsonConvertOutService.JsonPredef
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors, ServiceDataDto}

case class UaspDeserializationProcessFunction(implicit val serviceData: ServiceDataDto) extends DlqProcessFunction[Array[Byte], UaspDto, JsMaskedPathError] {

  override def processWithDlq(dto: Array[Byte]): Either[JsMaskedPathError, UaspDto] = {
    convert(dto) match {
      case Left(value) => Left(JsMaskedPathError(value.errors.mkString("\n")))
      case Right(value) => Right(value)
    }
  }

  def convert(value: Array[Byte]): Either[OutDtoWithErrors[UaspDto], UaspDto] = JsonConvertInService.deserialize[UaspDto](value)
}