package ru.vtb.uasp.filter

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.slf4j.LoggerFactory
import ru.vtb.uasp.common.abstraction.FlinkStreamPredef.StreamExecutionEnvironmentPredef
import ru.vtb.uasp.common.base.EnrichFlinkDataStream.EnrichFlinkDataStream
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.service.dto.KafkaDto
import ru.vtb.uasp.filter.configuration.property.FilterConfiguration
import ru.vtb.uasp.filter.configuration.property.FilterConfiguration.appPrefixDefaultName

object FilterJob {

  private val logger = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    logger.info("Start app: " + this.getClass.getName)

    val filterConfiguration = FilterConfiguration.configApp(appPrefixDefaultName, args)

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(filterConfiguration.executionEnvironmentProperty.syncParallelism)

    val configuredStream = init(env, filterConfiguration)

    val filteredStream: DataStream[UaspDto] = process(configuredStream, filterConfiguration)

    setSink(filteredStream, filterConfiguration)

    env.execute(filterConfiguration.executionEnvironmentProperty.appServiceName)
  }

  def process(dataStream: DataStream[UaspDto], filterConfiguration: FilterConfiguration): DataStream[UaspDto] = {
    dataStream
      .process(filterConfiguration.filterProcessFunction)
      .enrichName(s"${filterConfiguration.filterRule.tagPrefix}-filter")

  }

  def setOutSideSink(mainDataStream: DataStream[UaspDto], configuration: FilterConfiguration): Option[DataStreamSink[KafkaDto]] = {
    configuration.sinkService
      .outSideSink(mainDataStream)
  }

  private def setSink(mainDataStream: DataStream[UaspDto],
                      configuration: FilterConfiguration): Unit = {
    setMainSink(mainDataStream, configuration)
    setOutSideSink(mainDataStream, configuration)
  }


  def setMainSink(mainDataStream: DataStream[UaspDto],
                  configuration: FilterConfiguration): DataStreamSink[KafkaDto] = {
    configuration.sinkService
      .mainSink(mainDataStream)
  }

  private def init(env: StreamExecutionEnvironment, filterConfiguration: FilterConfiguration): DataStream[UaspDto] = {

    env
      .registerConsumer("input", filterConfiguration.kafkaSource, filterConfiguration.sinkService.errorsSink, filterConfiguration.deserializationProcessFunction)

  }

}