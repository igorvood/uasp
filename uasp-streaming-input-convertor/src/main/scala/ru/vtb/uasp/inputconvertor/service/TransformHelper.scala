package ru.vtb.uasp.inputconvertor.service

import org.json4s._
import play.api.libs.json._


object TransformHelper {
  implicit val formats: Formats = DefaultFormats


  def splitMessage[T](imMessage: JsValue, splitter: String)(implicit reads: Reads[T]) = {
    val contact = imMessage \\ splitter

    val seq3 = contact.flatMap(j => {

      val seq2 = j match {
        case JsArray(value) =>
          val seq1 = value.map { c => c.validate[T]
          }
          seq1
        case boolean: JsBoolean => List(JsError(s"Unable convert to array $splitter type JsBoolean"))
        case JsNull => List(JsError(s"Unable convert to array $splitter type JsNull"))
        case JsString(value) => List(JsError(s"Unable convert to array $splitter type JsString"))
        case JsObject(underlying) => List(JsError(s"Unable convert to array $splitter type JsObject"))
        case JsNumber(value) => List(JsError(s"Unable convert to array $splitter type JsNumber"))
      }
      seq2
    }
    )
    seq3
  }


}
