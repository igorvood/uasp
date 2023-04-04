package ru.vtb.uasp.mdm.enrichment.service.dto

import org.apache.flink.streaming.api.scala.DataStream
import ru.vtb.uasp.common.dto.UaspDto

case class StandartedDataStreams( // поток Way4 без глобального идентификатора
                                  mainDataStream: DataStream[UaspDto],
                                  // единый пток со всеми сообщениями с информацией по обогащению, сюда сливается информация с произвольного количества очередей
                                  commonStream: Option[DataStream[KeyedCAData]],
                                  globalIdStream: Option[DataStream[KeyedCAData]],
                                )
