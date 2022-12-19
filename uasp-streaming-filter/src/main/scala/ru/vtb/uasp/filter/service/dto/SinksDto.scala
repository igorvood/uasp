package ru.vtb.uasp.filter.service.dto

import org.apache.flink.streaming.api.datastream.DataStreamSink

case class SinksDto[T](mainSink: DataStreamSink[T],
                       outsideSink: Option[DataStreamSink[T]]
                      )
