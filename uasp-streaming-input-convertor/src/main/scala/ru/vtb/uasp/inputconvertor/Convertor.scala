package ru.vtb.uasp.inputconvertor

import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala._
import ru.vtb.uasp.common.abstraction.FlinkStreamProducerPredef.StreamFactory
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.kafka.FlinkSinkProperties.producerFactoryDefault
import ru.vtb.uasp.common.service.JsonConvertOutService.JsonPredef
import ru.vtb.uasp.common.service.dto.{KafkaDto, PropertyWithSerializer}
import ru.vtb.uasp.inputconvertor.entity.InputMessageType
import ru.vtb.uasp.inputconvertor.service.dto.UaspAndKafkaKey
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel.appPrefixDefaultName

object Convertor {

  def main(args: Array[String]): Unit = {
    val propsModel = initProps(args) // доделать рефакторинг, внедрить модель параметров

    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(propsModel.appSyncParallelism)

    val messageInputStream = init(env, propsModel)
    val mainDataStream = process(messageInputStream, propsModel)

    setSink(mainDataStream, propsModel)

    env.execute(propsModel.serviceData.fullServiceName)
  }

  def initProps(args: Array[String]): InputPropsModel = InputPropsModel.configApp(appPrefixDefaultName, args)

  def init(env: StreamExecutionEnvironment, propsModel: InputPropsModel): DataStream[InputMessageType] = {

    val consumer = propsModel.consumerProp.createConsumer(propsModel.inputMessageTypeDeserialization)

    env
      .addSource(consumer)
      .map(propsModel.consumerProp.prometheusMetric[InputMessageType](propsModel.serviceData))
      .rebalance

  }

  def process(
               messageInputStream: DataStream[InputMessageType],
               propsModel: InputPropsModel,
               producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
             ): DataStream[UaspAndKafkaKey] = {

    messageInputStream.processWithMaskedDqlFC(
      propsModel.serviceData,
      propsModel.uaspDtoConvertService,
      propsModel.sinkDlqProperty,
      producerFabric
    )
  }

  def setSink(mainDataStream: DataStream[UaspAndKafkaKey],
              propsModel: InputPropsModel,
              producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
             ): Unit = {

    setMainSink(mainDataStream, propsModel, producerFabric)
  }

  def setMainSink(mainDataStream: DataStream[UaspAndKafkaKey],
                  propsModel: InputPropsModel,
                  producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                 ): DataStreamSink[KafkaDto] = {

    val value = mainDataStream.maskedProducerF(
      propsModel.serviceData,
      PropertyWithSerializer(propsModel.outputSink, {
        _.serializeToKafkaJsValue
      }),
      propsModel.sinkDlqPropertyUaspAndKafkaKey,
      producerFabric
    )
    value
  }
}