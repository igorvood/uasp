package ru.vtb.uasp.pilot.model.vector.constants

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.OutputTag
import ru.vtb.uasp.common.dto.UaspDto

object Tags {
  val qAOutputTag: OutputTag[UaspDto] = OutputTag[UaspDto]("test")
  val case8OutputTag: OutputTag[UaspDto] = OutputTag[UaspDto]("case8OutputTag")
  val case38OutputTag: OutputTag[UaspDto] = OutputTag[UaspDto]("case38OutputTag")
  val case39OutputTag: OutputTag[UaspDto] = OutputTag[UaspDto]("case39OutputTag")
  val case39NewOutputTag: OutputTag[UaspDto] = OutputTag[UaspDto]("case39NewOutputTag")
  val case44OutputTag: OutputTag[UaspDto] = OutputTag[UaspDto]("case44OutputTag")
  val case56OutputTag: OutputTag[UaspDto] = OutputTag[UaspDto]("case56OutputTag")
  val case57OutputTag: OutputTag[UaspDto] = OutputTag[UaspDto]("case57OutputTag")
  val case71OutputTag: OutputTag[UaspDto] = OutputTag[UaspDto]("case71OutputTag")
  val case51OutputTag: OutputTag[UaspDto] = OutputTag[UaspDto]("case51OutputTag")
  val case48OutputTag: OutputTag[UaspDto] = OutputTag[UaspDto]("case48OutputTag")
  val case68OutputTag: OutputTag[UaspDto] = OutputTag[UaspDto]("case68OutputTag")

}
