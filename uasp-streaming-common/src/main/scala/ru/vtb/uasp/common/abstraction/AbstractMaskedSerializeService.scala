package ru.vtb.uasp.common.abstraction

import org.apache.flink.api.common.functions.RichMapFunction
import ru.vtb.uasp.common.mask.dto.{JsMaskedPath, JsMaskedPathError}
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors}

abstract class AbstractMaskedSerializeService[IN, OUT] extends RichMapFunction[IN, OUT]

abstract class AbstractDtoMaskedSerializeService[IN](val jsMaskedPath: Option[JsMaskedPath]) extends AbstractMaskedSerializeService[IN, Either[List[JsMaskedPathError], KafkaDto]] {


  override def map(value: IN): Either[List[JsMaskedPathError], KafkaDto] = {
    val errorsOrDto: Either[List[JsMaskedPathError], KafkaDto] = convert(value, jsMaskedPath)
    errorsOrDto
  }

  def convert(value: IN, jsMaskedPath: Option[JsMaskedPath]): Either[List[JsMaskedPathError], KafkaDto]
}

abstract class AbstractOutDtoWithErrorsMaskedSerializeService[IN](jsMaskedPath: Option[JsMaskedPath]) extends AbstractDtoMaskedSerializeService[OutDtoWithErrors[IN]](jsMaskedPath)

