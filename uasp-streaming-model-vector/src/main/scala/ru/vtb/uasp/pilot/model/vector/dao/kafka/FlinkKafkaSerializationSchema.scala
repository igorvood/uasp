package ru.vtb.uasp.pilot.model.vector.dao.kafka

import org.apache.flink.streaming.connectors.kafka.KafkaSerializationSchema
import org.apache.kafka.clients.producer.ProducerRecord
import play.api.libs.json.JsObject

import java.lang


class FlinkKafkaSerializationSchema(topic: String) extends KafkaSerializationSchema[JsObject] {
  override def serialize(value: JsObject, timestamp: lang.Long): ProducerRecord[Array[Byte], Array[Byte]] = {
    //TODO название поля customer_id взять из констант
    val key = value.fields.filter(x => x._1.equals("MDM_ID")).map(x=> x._2).mkString.replace("\"","")
    val producerRecord = new ProducerRecord(topic,key.getBytes() , value.toString().getBytes())
    producerRecord
  }

}
