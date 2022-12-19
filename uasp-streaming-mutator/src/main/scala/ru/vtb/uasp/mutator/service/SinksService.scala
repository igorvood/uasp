package ru.vtb.uasp.mutator.service

import org.apache.flink.streaming.api.scala.DataStream
import ru.vtb.uasp.common.dto.UaspDto

trait SinksService {

  def sinks(dataStream: DataStream[UaspDto]): Unit

}
