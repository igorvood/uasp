package ru.vtb.uasp.common.service

import org.apache.flink.api.scala.createTypeInformation
import ru.vtb.uasp.common.abstraction.DlqProcessFunction
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.service.JsonConvertOutService.JsonPredef
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors}

case class UaspDeserializationProcessFunction() extends DlqProcessFunction[Array[Byte], UaspDto, KafkaDto] {

  override def processWithDlq(dto: Array[Byte]): Either[KafkaDto, UaspDto] = {
    convert(dto) match {
      case Right(value) => Right(value)
      case Left(value) => Left(value.serializeToBytes)
    }
  }

  def convert(value: Array[Byte]): Either[OutDtoWithErrors, UaspDto] = JsonConvertInService.deserialize[UaspDto](value)
}