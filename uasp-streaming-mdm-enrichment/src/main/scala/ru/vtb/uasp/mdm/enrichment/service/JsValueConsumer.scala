package ru.vtb.uasp.mdm.enrichment.service

import org.apache.flink.streaming.api.scala.createTypeInformation
import play.api.libs.json.JsValue
import ru.vtb.uasp.common.abstraction.DlqProcessFunction
import ru.vtb.uasp.common.service.JsonConvertInService
import ru.vtb.uasp.common.service.JsonConvertOutService.JsonPredef
import ru.vtb.uasp.common.service.dto.KafkaDto

class JsValueConsumer extends DlqProcessFunction[Array[Byte], JsValue, KafkaDto] {

  override def processWithDlq(dto: Array[Byte]): Either[KafkaDto, JsValue] = {
    val value = JsonConvertInService.extractJsValue(dto)
    val dtoOrValue = value match {
      case Right(v) => Right(v)
      case Left(value) => Left(value.serializeToBytes)
    }
    dtoOrValue

  }


}
