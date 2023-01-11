package ru.vtb.uasp.pilot.model.vector.service

import org.apache.flink.api.common.functions.RichMapFunction
import play.api.libs.json._
import ru.vtb.uasp.common.dto.UaspDto


class JsonConverterService(mapFields: Array[String]) extends RichMapFunction[UaspDto, JsObject] {
  override def map(inMsg: UaspDto): JsObject = {
    modelToJson(inMsg)
  }

  def modelToJson(inMsg: UaspDto): JsObject = {
    implicit val mv: OWrites[UaspDto] = Json.writes[UaspDto]

    JsObject(flatten(Json.toJson(inMsg), "")
      .-("uuid")
      .-("customer_id")
      .-("id")
      .-("process_timestamp")
      .-("system-classification")
      .fields
    )

  }

  private def flatten(js: JsValue, fieldName: String): JsObject = js.as[JsObject].fields.foldLeft(Json.obj()) {
    case (acc, (k, v: JsObject)) =>
      if (mapFields.contains(fieldName) || fieldName.isEmpty) acc.deepMerge(flatten(v, k))
      else acc + (k, v)
    case (acc, (k, v)) => acc + (k, v)
  }

  def allKeys(json: JsValue): collection.Set[String] = json match {
    case o: JsObject => o.keys ++ o.values.flatMap(allKeys)
    case JsArray(as) => as.flatMap(allKeys).toSet
    case _ => Set()
  }
}
