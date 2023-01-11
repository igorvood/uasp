package ru.vtb.uasp.pilot.model.vector.dao.kafka

import org.apache.flink.api.common.functions.RichMapFunction
import play.api.libs.json.JsObject
import ru.vtb.uasp.common.service.dto.KafkaDto

class KafkaDtoSerializationService extends RichMapFunction[JsObject, KafkaDto] {


  override def map(value: JsObject): KafkaDto = {
    //TODO название поля customer_id взять из констант
    val key = value.fields.filter(x => x._1.equals("MDM_ID")).map(x => x._2).mkString.replace("\"", "")
    KafkaDto(key.getBytes(), value.toString().getBytes())

  }

}
