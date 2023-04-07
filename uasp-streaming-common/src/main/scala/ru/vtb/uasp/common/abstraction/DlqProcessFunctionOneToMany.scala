package ru.vtb.uasp.common.abstraction

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector

abstract class DlqProcessFunctionOneToMany[I, O, DLQ: TypeInformation] extends AbstractDlqProcessFunction[I, O, DLQ] {

  def processWithDlq(dto: I): List[Either[DLQ, O]]

  override def processElement(value: I, ctx: ProcessFunction[I, O]#Context, out: Collector[O]): Unit = {
    processWithDlq(value).foreach {
      case Right(v) => out.collect(v)
      case Left(value) => ctx.output[DLQ](dlqOutPut, value)
    }
  }
}

