package ru.vtb.uasp.filter.service

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.OutputTag
import ru.vtb.uasp.common.dto.UaspDto

object OutTag {
  def outputTagsF(prefix: String): OutputTag[UaspDto] = OutputTag[UaspDto](outputTagsName(prefix))

  def outputTagsName(prefix: String): String = s"$prefix-success"

  def outputTagsErrsF(prefix: String): OutputTag[UaspDto] = OutputTag[UaspDto](outputTagsErrsName(prefix))

  def outputTagsErrsName(prefix: String): String = s"$prefix-error"
}
