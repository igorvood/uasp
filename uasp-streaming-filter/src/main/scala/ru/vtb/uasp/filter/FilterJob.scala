package ru.vtb.uasp.filter

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.slf4j.LoggerFactory
import ru.vtb.uasp.common.abstraction.FlinkStreamPredef.{StreamExecutionEnvironmentPredef, StreamFactory}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.kafka.FlinkSinkProperties.producerFactoryDefault
import ru.vtb.uasp.common.service.JsonConvertOutService.IdentityPredef
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

  def process(dataStream: DataStream[UaspDto],
              filterConfiguration: FilterConfiguration,
              producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
             ): DataStream[UaspDto] = {
    dataStream
      .processAndDlqSinkWithMetric(
        process = filterConfiguration.filterProcessFunction,
        sinkDlqFunction = filterConfiguration.flinkSinkPropertiesErr,
        producerFactory = producerFabric
      )

  }

  private def setSink(mainDataStream: DataStream[UaspDto],
                      configuration: FilterConfiguration): Unit = {
    setMainSink(mainDataStream, configuration)
  }


  def setMainSink(mainDataStream: DataStream[UaspDto],
                  configuration: FilterConfiguration,
                  producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                 ): DataStreamSink[KafkaDto] = {
    val mainSink = configuration.flinkSinkPropertiesOk.createSinkFunction(producerFabric)
    mainDataStream
      .map(_.serializeToBytes)
      .map(configuration.flinkSinkPropertiesOk.prometheusMetric[KafkaDto])
      .addSink(mainSink)
  }

  private def init(env: StreamExecutionEnvironment,
                   filterConfiguration: FilterConfiguration,
                   producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                  ): DataStream[UaspDto] = {
    env
      .registerConsumerWithMetric(
        consumerProperties = filterConfiguration.consumerPropperty,
        dlqProducer = filterConfiguration.flinkSinkPropertiesErr,
        serialisationProcessFunction = filterConfiguration.deserializationProcessFunction,
        producerFactory = producerFabric
      )
  }

}