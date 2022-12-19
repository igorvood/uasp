package ru.vtb.uasp.mutator

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.filter.configuration.property.FilterConfiguration.appPrefixDefaultName
import ru.vtb.uasp.mutator.configuration.property.MutationConfiguration


object DroolsBusinessRullesJob {

  def main(args: Array[String]): Unit = {

    val mutationConfiguration = MutationConfiguration.configApp(appPrefixDefaultName, args)

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(mutationConfiguration.businessExecutionEnvironmentProperty.syncParallelism)

    val uaspDtoStream = init(env)(mutationConfiguration.kafkaUaspDto)
      .map(d => mutationConfiguration.convertInMapService.deserialize[UaspDto](d).right.get)

    val mutatedUaspStream = process(mutationConfiguration, uaspDtoStream)

    setSink(mutationConfiguration, mutatedUaspStream)

    env.execute(mutationConfiguration.businessExecutionEnvironmentProperty.appServiceName)
  }

  private def process(mutationConfiguration: MutationConfiguration, uaspDtoStream: DataStream[UaspDto]) = {
    uaspDtoStream
      .map(mutationConfiguration.mutateService)
      .process(mutationConfiguration.filterProcessFunction)
  }

  private def setSink(mutationConfiguration: MutationConfiguration, mutatedUaspStream: DataStream[UaspDto]): Unit = {
    setMainSink(mutationConfiguration, mutatedUaspStream)
    setOutSideSink(mutationConfiguration, mutatedUaspStream)
  }

  def setMainSink(mutationConfiguration: MutationConfiguration, mutatedUaspStream: DataStream[UaspDto]) = {
    mutationConfiguration.kafkaSinksService.mainSink(mutatedUaspStream)
  }

  def setOutSideSink(mutationConfiguration: MutationConfiguration, mutatedUaspStream: DataStream[UaspDto]) = {
    mutationConfiguration.kafkaSinksService.outSideSink(mutatedUaspStream)
  }


  private def init(env: StreamExecutionEnvironment)(implicit f: SourceFunction[Array[Byte]]): DataStream[Array[Byte]] = {
    env.addSource(f)
  }

}
