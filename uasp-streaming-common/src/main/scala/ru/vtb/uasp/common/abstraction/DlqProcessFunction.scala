package ru.vtb.uasp.common.abstraction

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.OutputTag
import org.apache.flink.util.Collector

abstract class DlqProcessFunction[I, O, DLQ: TypeInformation] extends ProcessFunction[I, O] {

  val dlqOutPut: OutputTag[DLQ] = OutputTag[DLQ]("dlq_tag")

  def processWithDlq(dto: I): Either[DLQ, O]

  override def processElement(value: I, ctx: ProcessFunction[I, O]#Context, out: Collector[O]): Unit = {
    processWithDlq(value) match {
      case Right(v) => out.collect(v)
      case Left(value) => ctx.output[DLQ](dlqOutPut, value)
    }
  }
}

