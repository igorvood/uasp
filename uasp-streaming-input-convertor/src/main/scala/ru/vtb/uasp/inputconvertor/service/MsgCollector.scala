package ru.vtb.uasp.inputconvertor.service

import org.apache.flink.util.Collector
import ru.vtb.uasp.inputconvertor.entity.CommonMessageType

import java.util

class MsgCollector extends Collector[CommonMessageType] {
  private val list = new util.ArrayList[CommonMessageType]

  def getAll(): util.ArrayList[CommonMessageType] = list

  override def collect(t: CommonMessageType): Unit = list.add(t)

  override def close(): Unit = {
    //list.clear()
  }
}
