package ru.vtb.uasp.inputconvertor.utils.serialization

import org.apache.flink.api.common.functions.RichMapFunction
import play.api.libs.json.{Json, OWrites, Reads}
import ru.vtb.uasp.common.service.JsonConvertOutService.JsonPredef
import ru.vtb.uasp.common.service.dto.KafkaDto
import ru.vtb.uasp.inputconvertor.entity.CommonMessageType

class DlqPullOut extends RichMapFunction[CommonMessageType, KafkaDto] {
  override def map(element: CommonMessageType): KafkaDto = {
    // TODO переработать
    Errr(element.message_key, element.error, element.valid)
      .serializeToBytes(None).right.get
  }
}

case class Errr(message_key: String,
                error: Option[String],
                valid: Boolean,
               )

object Errr {
  implicit val uaspJsonReads: Reads[Errr] = Json.reads[Errr]
  implicit val uaspJsonWrites: OWrites[Errr] = Json.writes[Errr]

}
