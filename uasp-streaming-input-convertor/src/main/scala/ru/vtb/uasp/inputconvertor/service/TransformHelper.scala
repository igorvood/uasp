package ru.vtb.uasp.inputconvertor.service

import org.apache.flink.util.Collector
import org.json4s._
import org.json4s.jackson.JsonMethods._
import play.api.libs.json.JsValue
import ru.vtb.uasp.common.service.dto.OutDtoWithErrors
import ru.vtb.uasp.inputconvertor.constants.Config
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

import scala.collection.mutable.ListBuffer


object TransformHelper {
  implicit val formats: Formats = DefaultFormats

  def extractJson(inputMessage: InputMessageType,
                  allProps: InputPropsModel,
                  collector: Collector[Either[OutDtoWithErrors[JsValue], CommonMessageType]]
                 ): Unit = {


    def extractMessage(parsedValue: JValue): JValue = {
      allProps.messageJsonPath
        .map(path => {
          val payloadMessageJV = parsedValue \ path
          val payloadMessage: String = payloadMessageJV.extract[String]
          parse(payloadMessage)
        }
        )
        .getOrElse(parsedValue)
    }

    def splitMessage(message_str: String) = {
      val list = ListBuffer[JValue]()

      val parsedMessage = parse(message_str)
      allProps.jsonSplitElement
        .map(splitter => {
          val contacts = parsedMessage \\ splitter
          val l = List(contacts)
          for (JArray(o) <- l) {
            for (d <- o) {
              val merged = parsedMessage mapField {
                case (splitter, JArray(arr)) => (splitter, JArray(List(d)))
                case other => other
              }
              list += merged
            }

          }
        })
        .getOrElse(list += parsedMessage)
      list.toList
    }

    val message_str = new String(inputMessage.message, Config.charset)
    val cm: CommonMessageType = CommonMessageType(message_key = inputMessage.message_key)
    try {
      val splitValue = splitMessage(message_str)
      for (value <- splitValue) {
        val result: Either[OutDtoWithErrors[JsValue], CommonMessageType] = if (!inputMessage.message.isEmpty) {

          val parsedValue = value

          val message = extractMessage(parsedValue)
          Right(cm.copy( json_message = Some(message)))

        }
        else
          Left(OutDtoWithErrors[JsValue](allProps.serviceData,Some(this.getClass.getName), List("Message " + inputMessage.message_key + " is empty"), None))

        collector.collect(result)
      }
    } catch {
      case e: Exception =>
        collector.collect(
          Left(OutDtoWithErrors[JsValue](allProps.serviceData,Some(this.getClass.getName), List("Error json parsing: "
            + e.getMessage
            + ", with allProps: " + allProps.dtoMap.filterKeys(key => !key.contains("password"))), None))
//          cm.copy(error = Some(
//                    "Error json parsing: "
//            + e.getMessage
//            + ", with allProps: " + allProps.dtoMap.filterKeys(key => !key.contains("password"))
//          )
//          )
        )
    }
  }

}
