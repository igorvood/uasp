package ru.vtb.uasp.inputconvertor.service

import org.apache.flink.util.Collector
import org.json4s._
import org.json4s.jackson.JsonMethods._
import ru.vtb.uasp.inputconvertor.constants.Config
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

import scala.collection.mutable.ListBuffer


object TransformHelper {
  implicit val formats: Formats = DefaultFormats

  def extractJson(inputMessage: InputMessageType,
                  allProps: InputPropsModel,
                  collector: Collector[CommonMessageType]
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

    def splitMessage(cm: CommonMessageType) = {
      val list = ListBuffer[JValue]()
      val parsedMessage = parse(cm.message_str.get)
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

    val cm: CommonMessageType = CommonMessageType(message_key = inputMessage.message_key,
      message = inputMessage.message,
      message_str = Some(new String(inputMessage.message, Config.charset)))
    try {
      val splitValue = splitMessage(cm)
      for (value <- splitValue) {
        val result = if (!inputMessage.message.isEmpty) {

          val parsedValue = value

          val message = extractMessage(parsedValue)
          cm.copy(valid = true, json_message = Some(message))

        }
        else
          cm.copy(error = Some("Message " + inputMessage.message_key + " is empty"))

        collector.collect(result)
      }
    } catch {
      case e: Exception =>
        collector.collect(
          cm.copy(error = Some("Error json parsing: "
            + e.getMessage
            + ", with allProps: " + allProps.dtoMap.filterKeys(key => !key.contains("password"))
          )
          )
        )
    }
  }

}
