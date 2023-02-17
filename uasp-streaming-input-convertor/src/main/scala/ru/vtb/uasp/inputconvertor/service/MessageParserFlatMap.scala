package ru.vtb.uasp.inputconvertor.service

import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.util.Collector
import play.api.libs.json.JsValue
import ru.vtb.uasp.common.service.dto.OutDtoWithErrors
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.service.TransformHelper.extractJson
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

class MessageParserFlatMap(config: InputPropsModel)
  extends RichFlatMapFunction[InputMessageType, Either[OutDtoWithErrors[JsValue], CommonMessageType]] {
  override def flatMap(m: InputMessageType, collector: Collector[Either[OutDtoWithErrors[JsValue], CommonMessageType]]): Unit = {
    extractJson(m, config, collector)
  }
}
