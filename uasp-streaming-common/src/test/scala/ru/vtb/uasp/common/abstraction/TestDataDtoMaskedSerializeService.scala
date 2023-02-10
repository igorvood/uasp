package ru.vtb.uasp.common.abstraction

import ru.vtb.uasp.common.mask.dto.{JsMaskedPath, JsMaskedPathError}
import ru.vtb.uasp.common.service.JsonConvertOutService.JsonPredef
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors}

class TestDataDtoMaskedSerializeService(jsMaskedPath: Option[JsMaskedPath]) extends AbstractOutDtoWithErrorsMaskedSerializeService[TestDataDto](jsMaskedPath) {
  override def convert(value: OutDtoWithErrors[TestDataDto], jsMaskedPath: Option[JsMaskedPath]): Either[List[JsMaskedPathError], KafkaDto] = {
    value.serializeToBytes(jsMaskedPath)
  }
}