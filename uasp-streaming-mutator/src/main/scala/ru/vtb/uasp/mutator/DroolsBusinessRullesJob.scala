package ru.vtb.uasp.mutator

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import ru.vtb.uasp.common.abstraction.FlinkStreamProducerPredef.{StreamExecutionEnvironmentPredef, StreamFactory}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.kafka.FlinkSinkProperties.producerFactoryDefault
import ru.vtb.uasp.common.service.JsonConvertOutService.{IdentityPredef, JsonPredef}
import ru.vtb.uasp.common.service.dto.KafkaDto
import ru.vtb.uasp.filter.configuration.property.FilterConfiguration.appPrefixDefaultName
import ru.vtb.uasp.mutator.configuration.property.MutationConfiguration


object DroolsBusinessRullesJob {

  def main(args: Array[String]): Unit = {

    val mutationConfiguration = MutationConfiguration.configApp(appPrefixDefaultName, args)

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(mutationConfiguration.businessExecutionEnvironmentProperty.syncParallelism)

    val uaspDtoStream = init(env, mutationConfiguration)

    val mutatedUaspStream = process(mutationConfiguration, uaspDtoStream)

    setSink(mutatedUaspStream, mutationConfiguration)

    env.execute(mutationConfiguration.businessExecutionEnvironmentProperty.serviceDto.fullServiceName)
  }

  private def process(mutationConfiguration: MutationConfiguration,
                      uaspDtoStream: DataStream[UaspDto],
                      producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                     ) = {

    val mutation = uaspDtoStream
      .processWithMaskedDqlF(
        serviceData = mutationConfiguration.businessExecutionEnvironmentProperty.serviceDto,
        process = mutationConfiguration.newMutateService,
        sinkDlqProperty = mutationConfiguration.flinkSinkPropertiesErr.map(sp => sp -> { (q, w) => q.serializeToBytes(w) }),
        producerFactory = producerFabric
      )
      .processWithMaskedDqlF(
        serviceData = mutationConfiguration.businessExecutionEnvironmentProperty.serviceDto,
        process = mutationConfiguration.filterProcessFunction,
        sinkDlqProperty = mutationConfiguration.flinkSinkPropertiesErr.map(sp => sp -> { (q, w) => q.serializeToBytes(w) }),
        producerFactory = producerFabric
      )
    mutation
  }

  private def setSink(mainDataStream: DataStream[UaspDto],
                      configuration: MutationConfiguration): Unit = {
    setMainSink(mainDataStream, configuration)
  }

  def setMainSink(mainDataStream: DataStream[UaspDto],
                  configuration: MutationConfiguration,
                  producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                 ): DataStreamSink[KafkaDto] = {
    val mainSink = configuration.flinkSinkPropertiesOk.createSinkFunction(producerFabric)
    mainDataStream
      .map(q => q.serializeToBytes(None).right.get)
      .map(configuration.flinkSinkPropertiesOk.prometheusMetric[KafkaDto](configuration.businessExecutionEnvironmentProperty.serviceDto))
      .addSink(mainSink)
  }

  private def init(env: StreamExecutionEnvironment,
                   mutationConfiguration: MutationConfiguration,
                   producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                  ): DataStream[UaspDto] = {
    env
      .registerConsumerWithMetric(
        mutationConfiguration.businessExecutionEnvironmentProperty.serviceDto,
        consumerProperties = mutationConfiguration.consumerPropperty,
        dlqProducer = mutationConfiguration.flinkSinkPropertiesErr,
        serialisationProcessFunction = mutationConfiguration.deserializationProcessFunction,
        producerFactory = producerFabric
      )

  }

}
