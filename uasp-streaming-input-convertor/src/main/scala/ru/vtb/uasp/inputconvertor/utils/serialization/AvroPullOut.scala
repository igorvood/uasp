package ru.vtb.uasp.inputconvertor.utils.serialization

import org.apache.flink.api.common.functions.RichMapFunction
import ru.vtb.uasp.common.service.JsonConvertOutService.IdentityPredef
import ru.vtb.uasp.common.service.dto.KafkaDto
import ru.vtb.uasp.inputconvertor.constants.Config
import ru.vtb.uasp.inputconvertor.entity.CommonMessageType
import ru.vtb.uasp.inputconvertor.service.dto.UaspAndKafkaKey

class AvroPullOut extends RichMapFunction[UaspAndKafkaKey, KafkaDto] {
  override def map(element: UaspAndKafkaKey): KafkaDto = {
    val dto = KafkaDto(
      element.kafkaKey.getBytes(Config.charset),
      element.uaspDto.serializeToBytes(None).right.get.value
    )
    dto
  }
}
