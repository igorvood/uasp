package ru.vtb.uasp.inputconvertor.service

import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.util.Collector
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.service.TransformHelper.extractJson
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

class MessageParserFlatMap(config: InputPropsModel)
  extends RichFlatMapFunction[InputMessageType, CommonMessageType] {
  override def flatMap(m: InputMessageType, collector: Collector[CommonMessageType]): Unit = {
    extractJson(m, config, collector)
  }
}
