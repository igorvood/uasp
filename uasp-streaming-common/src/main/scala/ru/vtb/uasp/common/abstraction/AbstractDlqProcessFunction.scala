package ru.vtb.uasp.common.abstraction

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.OutputTag

abstract class AbstractDlqProcessFunction [I, O, DLQ: TypeInformation] extends ProcessFunction[I, O] {
  val dlqOutPut: OutputTag[DLQ] = OutputTag[DLQ]("dlq_tag")
}
