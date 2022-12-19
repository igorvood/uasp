package ru.vtb.uasp.filter.service

import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.scala.DataStream
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.service.dto.KafkaDto
import ru.vtb.uasp.filter.service.dto.SinksDto

trait SinksService {

  def sinks(dataStream: DataStream[UaspDto]): SinksDto[KafkaDto]=
    SinksDto(mainSink(dataStream), outSideSink(dataStream))

  def mainSink(dataStream: DataStream[UaspDto]): DataStreamSink[KafkaDto]

  def outSideSink(dataStream: DataStream[UaspDto]): Option[DataStreamSink[KafkaDto]]

}
