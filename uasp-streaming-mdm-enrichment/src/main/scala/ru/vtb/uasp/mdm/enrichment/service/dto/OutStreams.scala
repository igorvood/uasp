package ru.vtb.uasp.mdm.enrichment.service.dto

import org.apache.flink.streaming.api.scala.DataStream
import ru.vtb.uasp.common.dto.UaspDto

case class OutStreams(mainStream: DataStream[UaspDto])
