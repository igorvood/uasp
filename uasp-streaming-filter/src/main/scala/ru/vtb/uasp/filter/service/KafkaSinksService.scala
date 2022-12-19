package ru.vtb.uasp.filter.service

import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.{DataStream, OutputTag, createTypeInformation}
import org.slf4j.LoggerFactory
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.service.JsonConvertOutService
import ru.vtb.uasp.common.service.dto.KafkaDto
import ru.vtb.uasp.filter.configuration.property.{ExecutionFlinkEnvironmentProperty, FilterRule}
import ru.vtb.uasp.filter.service.OutTag.{outputTagsErrsF, outputTagsErrsName, outputTagsName}

case class KafkaSinksService(
                              filterConfig: FilterRule,
                              prop: ExecutionFlinkEnvironmentProperty,
                              producers: Map[String, SinkFunction[KafkaDto]]
                            ) extends SinksService {

  private val successName = outputTagsName(filterConfig.tagPrefix)
  private val errorsName = outputTagsErrsName(filterConfig.tagPrefix)

  val outputTagErr: OutputTag[UaspDto] = outputTagsErrsF(filterConfig.tagPrefix)
  private val convertOutMapService = JsonConvertOutService
  val successSink: SinkFunction[KafkaDto] = producers.getOrElse(successName, throw new IllegalStateException(s"unable to found producer with name $successName"))
  val errorsSink: Option[SinkFunction[KafkaDto]] = producers.get(errorsName)

  private val ourOkStr = "-sink-outFilteredSink"
  private val ourErrStr = "-sink-errorOutFilteredSink"

  override def mainSink(dataStream: DataStream[UaspDto]): DataStreamSink[KafkaDto] =
    dataStream
      .map(uaspDto => convertOutMapService.serializeToBytes(uaspDto))
      .addSink(successSink)
      .name(s"$successName$ourOkStr")
      .uid(s"$successName$ourOkStr")


  override def outSideSink(dataStream: DataStream[UaspDto]): Option[DataStreamSink[KafkaDto]] = {
    errorsSink
      .map(sink => {
        dataStream
          .getSideOutput(outputTagErr)
          //          .print()
          .map(uaspDto => convertOutMapService.serializeToBytes(uaspDto))
          .addSink(sink)
          .name(s"$errorsName$ourErrStr")
          .uid(s"$errorsName$ourErrStr")
      }
      )
  }
}

