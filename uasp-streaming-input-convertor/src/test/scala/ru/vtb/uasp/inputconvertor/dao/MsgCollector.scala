package ru.vtb.uasp.inputconvertor.dao

import org.apache.flink.util.Collector
import play.api.libs.json.JsValue
import ru.vtb.uasp.common.service.dto.OutDtoWithErrors
import ru.vtb.uasp.inputconvertor.entity.CommonMessageType

import java.util

class MsgCollector extends Collector[Either[OutDtoWithErrors[JsValue], CommonMessageType]] {
  private val list = new util.ArrayList[Either[OutDtoWithErrors[JsValue], CommonMessageType]]

  def getAll(): util.ArrayList[Either[OutDtoWithErrors[JsValue], CommonMessageType]] = list

  override def collect(t: Either[OutDtoWithErrors[JsValue], CommonMessageType]): Unit = list.add(t)

  override def close(): Unit = {

  }
}
