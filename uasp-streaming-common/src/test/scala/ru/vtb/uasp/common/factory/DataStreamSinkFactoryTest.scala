/*
package ru.vtb.uasp.common.factory

import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.{DataStream, OutputTag}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer
import org.mockito.Mockito
import org.mockito.Mockito.{times, verify, when}
import ru.vtb.uasp.common.abstraction.AbstractSpec
import ru.vtb.uasp.common.base.{OutSideSinkService, SinkService}
import ru.vtb.uasp.common.factory.DataStreamSinkFactory.SinkFactory


class DataStreamSinkFactoryTest extends AbstractSpec {

  behavior of "SinkFactory methods test"

  it should "sink function test, should be called map and addSink methods" in withSteamEnv { env =>

    val inputDataStream = Mockito.mock(classOf[DataStream[Int]])
    val secondDataStream = Mockito.mock(classOf[DataStream[Int]])
    val mapFun = Mockito.mock(classOf[RichMapFunction[Int, Int]])
    val sinkFun = Mockito.mock(classOf[FlinkKafkaProducer[Int]])
    val sinkFactory = SinkFactory(inputDataStream)

    when(inputDataStream.map(mapFun)).thenReturn(secondDataStream)
    sinkFactory.sink(mapFun, sinkFun)

    verify(inputDataStream, times(1)).map(mapFun)
    verify(secondDataStream, times(1)).addSink(sinkFun)
  }

  it should "sink function test, should be called sink method" in withSteamEnv { env =>

    val sinkService = Mockito.mock(classOf[SinkService[Int, Long]])
    val inputDataStream = Mockito.mock(classOf[DataStream[Int]])

    val sinkFactory = SinkFactory(inputDataStream)

    sinkFactory.sink(sinkService)

    verify(sinkService, times(1)) sink inputDataStream
  }

  it should "outSideSink function test, should be called getSideOutput and sink methods" in withSteamEnv { env =>

    val inputDataStream = Mockito.mock(classOf[DataStream[Int]])
    val secondDataStream = Mockito.mock(classOf[DataStream[Int]])
    val thirdDataStream = Mockito.mock(classOf[DataStream[Int]])
    val mapFun = Mockito.mock(classOf[RichMapFunction[Int, Int]])
    val sinkFun = Mockito.mock(classOf[FlinkKafkaProducer[Int]])
    val sinkFactory = SinkFactory(inputDataStream)

    val outputTag = new OutputTag[Int]("Int tag")

    when(inputDataStream.getSideOutput(outputTag)) thenReturn secondDataStream
    when(secondDataStream.map(mapFun)) thenReturn thirdDataStream

    sinkFactory outSideSink(outputTag, mapFun, sinkFun)

    verify(inputDataStream, times(1)) getSideOutput outputTag
    verify(secondDataStream, times(1)) map mapFun
    verify(thirdDataStream, times(1)) addSink sinkFun

  }

  it should "outSideSink function test, should be called outsideSink method " in withSteamEnv { env =>

    val outSideSinkService = Mockito.mock(classOf[OutSideSinkService[Int, Long]])

    val inputDataStream = Mockito.mock(classOf[DataStream[Int]])
    val sinkFactory = SinkFactory(inputDataStream)

    sinkFactory outSideSink outSideSinkService

    verify(outSideSinkService, times(1)) outsideSink inputDataStream
  }

}


*/
