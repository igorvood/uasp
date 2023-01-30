package ru.vtb.uasp.inputconvertor.utils.serialization

import org.apache.flink.api.common.functions.RichMapFunction
import ru.vtb.uasp.common.service.dto.KafkaDto
import ru.vtb.uasp.inputconvertor.constants.Config
import ru.vtb.uasp.inputconvertor.entity.CommonMessageType

class NewAvroPullOut extends RichMapFunction[CommonMessageType, KafkaDto] {
  override def map(element: CommonMessageType): KafkaDto = KafkaDto(
    element.message_key.getBytes(Config.charset),
    element.avro_message.get
  )
}
