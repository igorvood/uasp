package ru.vtb.uasp.common.utils.json

import com.sksamuel.avro4s.AvroSchema
import org.apache.avro.Schema
import play.api.libs.json.{JsObject, JsValue, Json, OWrites}
import ru.vtb.uasp.common.dto.UaspDto

import scala.collection.JavaConverters.asScalaBufferConverter

object JsonConverter {
  var mapFields: Array[String] = getMapFieldsNames(AvroSchema[UaspDto])

  //делает плоский Json из объекта UaspDto
  def modelToJson(obj: UaspDto): JsObject = {
    implicit val mv: OWrites[UaspDto] = Json.writes[UaspDto]
    flatten(Json.toJson(obj), "")
  }

  def flatten(js: JsValue, fieldName: String): JsObject = js.as[JsObject].fields.foldLeft(Json.obj()) {
    case (acc, (k, v: JsObject)) =>
      if (mapFields.contains(fieldName) || fieldName.isEmpty) acc.deepMerge(flatten(v, k))
      else acc + (k, v)
    case (acc, (k, v)) => acc + (k, v)
  }

  def filter(js: JsValue, fieldName: String): JsObject = js.as[JsObject].fields.foldLeft(Json.obj()) {
    case (acc, (k, v: JsObject)) =>
      if (k.equals("customer_id")) acc.deepMerge(flatten(v, fieldName))
      else acc
    case (acc, (k, v)) => acc + (k, v)
  }

  def getMapFieldsNames(schema: Schema): Array[String] = {
    schema.getFields.asScala.map(_.name()).filter(x => schema.getField(x).schema().getType.equals("MAP")).toArray
  }

  def getFields(js: JsValue): scala.collection.Set[String] = js.as[JsObject].fieldSet.map(x => x._1)

  def getFieldsJS(js: JsValue): scala.collection.Set[(String, JsValue)] = js.as[JsObject].fieldSet

  def getValues(js: JsValue): Iterable[JsValue] = js.as[JsObject].values
}
