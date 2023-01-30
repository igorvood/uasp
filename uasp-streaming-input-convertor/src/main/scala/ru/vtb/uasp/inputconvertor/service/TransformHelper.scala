package ru.vtb.uasp.inputconvertor.service

import org.apache.flink.util.Collector
import org.json4s._
import org.json4s.jackson.JsonMethods._
import ru.vtb.uasp.inputconvertor.constants.Config
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}

import scala.collection.mutable.ListBuffer


object TransformHelper {
  implicit val formats: Formats = DefaultFormats

  def extractJson(inputMessage: InputMessageType, allProps: Map[String, String], defaultJsonSchemaKey: String, collector: Collector[CommonMessageType]): Unit = {


    def extractMessage(parsedValue: JValue): JValue =
      if (allProps.getOrElse("app.message.json.path", "") != "") {
        val payloadMessageJV = parsedValue \ allProps("app.message.json.path")
        val payloadMessage: String = payloadMessageJV.extract[String]
        parse(payloadMessage)
      }
      else parsedValue

    def splitMessage(cm: CommonMessageType) = {
      val list = ListBuffer[JValue]()
      val parsedMessage = parse(cm.message_str.get)

      if (allProps.getOrElse("input-convertor.json.split.element", "") != "") {
        /*val doc1 = render(parsedMessage)
        val compactJson1 = compact(doc1)
        val prettyJson1 = pretty(doc1)
        println(prettyJson1)*/
        val splitter = allProps("input-convertor.json.split.element")
        val contacts = parsedMessage \\ splitter

        //FIXME https://github.com/json4s/json4s/issues/428
        /*for (
          JObject(elems) <- parsedMessage;
          ( "contact", JArray(arr)) <- elems
        )*/
        /*for ( o <- arr) {
                    val merged = parsedMessage mapField {
                      case ("contact", JArray(arr)) => ("contact", JArray(List(o)))
                      case other => other
                    }
                    list += merged
                  }*/
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

      } else list += parsedMessage

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

          val jsonSchemaKey: String = ""
          val message = extractMessage(parsedValue)
          cm.copy(valid = true, json_message = Some(message), json_schemakey = Some(jsonSchemaKey))

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
            + ", with allProps: "
            + allProps.filterKeys(key => !key.contains("password")))
          )
        )
    }
  }

}
