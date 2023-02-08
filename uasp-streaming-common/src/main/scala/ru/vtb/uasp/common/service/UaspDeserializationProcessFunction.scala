package ru.vtb.uasp.common.service

import org.apache.flink.api.scala.createTypeInformation
import ru.vtb.uasp.common.abstraction.DlqProcessFunction
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.service.JsonConvertOutService.JsonPredef
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors, ServiceDataDto}

case class UaspDeserializationProcessFunction(implicit val serviceDataDto: ServiceDataDto) extends DlqProcessFunction[Array[Byte], UaspDto, KafkaDto] {

  override def processWithDlq(dto: Array[Byte]): Either[KafkaDto, UaspDto] = {
    convert(dto) match {
      case Right(value) => Right(value)
      case Left(uaspDtoConvertValue) =>
        val errorsOrDto = uaspDtoConvertValue.serializeToBytes(None)
        val value1 = errorsOrDto match {
          case Left(uaspDtoMaskConvertValue) =>
            OutDtoWithErrors[UaspDto](
             serviceDataDto = serviceDataDto,
             errorPosition = Some(UaspDeserializationProcessFunction.getClass.getName),
              errors = uaspDtoConvertValue.errors ++ uaspDtoMaskConvertValue.map( q=> q.error),
              data = None
            )
             .serializeToBytes
          case Right(value) => value
        }

        Left(value1)
    }
  }

  def convert(value: Array[Byte]): Either[OutDtoWithErrors[UaspDto], UaspDto] = JsonConvertInService.deserialize[UaspDto](value)
}