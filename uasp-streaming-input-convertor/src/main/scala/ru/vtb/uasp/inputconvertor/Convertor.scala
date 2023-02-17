package ru.vtb.uasp.inputconvertor

import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector
import play.api.libs.json.JsValue
import ru.vtb.uasp.common.abstraction.DlqProcessFunction
import ru.vtb.uasp.common.abstraction.FlinkStreamProducerPredef.{StreamExecutionEnvironmentPredef, StreamFactory}
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.kafka.FlinkSinkProperties.producerFactoryDefault
import ru.vtb.uasp.common.service.JsonConvertOutService.JsonPredef
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors}
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.service.ConvertHelper.validAndTransform
import ru.vtb.uasp.inputconvertor.service._
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel.appPrefixDefaultName
import ru.vtb.uasp.inputconvertor.utils.serialization.{AvroPullOut, DlqPullOut}

object Convertor {

  private val outputTag = OutputTag[CommonMessageType]("dlq")

  def main(args: Array[String]): Unit = {
    val propsModel = initProps(args) // доделать рефакторинг, внедрить модель параметров

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val messageInputStream = init(env, propsModel)
    // extract to json
    val mainDataStream = process(messageInputStream, propsModel)
    // get result
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
             ): DataStream[CommonMessageType] = {
    val messageParserFlatMap = new MessageParserFlatMap(propsModel)

    val extractJsonStream = messageInputStream
      .flatMap(messageParserFlatMap)
      .processWithMaskedDqlFC[CommonMessageType, JsValue](
        propsModel.serviceData,
        new DlqProcessFunction[Either[OutDtoWithErrors[JsValue], CommonMessageType],CommonMessageType,OutDtoWithErrors[JsValue] ] {
          override def processWithDlq(dto: Either[OutDtoWithErrors[JsValue], CommonMessageType]): Either[OutDtoWithErrors[JsValue], CommonMessageType] = dto
        },
        Some(propsModel.dlqSink->{(q,w)=> {
          q.serializeToBytes(w)(OutDtoWithErrors.outDtoWithErrorsJsonWrites[JsValue](OutDtoWithErrors.writesJsValue))

        }}),
        producerFabric
      )

//    val extractJsonStream: DataStream[CommonMessageType] =
//      value1
//        .map(q=>q.right.get)
//        .name(propsModel.savepointPref + "-flatMap-messageInputStream").uid(propsModel.savepointPref + "-flatMap-messageInputStream")


    val commonStream = extractJsonStream
      //TODO: avro schema inference only once
      .map(m => validAndTransform(m, propsModel))
      .name(propsModel.savepointPref + "-map-validAndTransform").uid(propsModel.savepointPref + "-map-validAndTransform")

    //split valid and invalid messages
    commonStream
      .process((value: CommonMessageType, ctx: ProcessFunction[CommonMessageType,
        CommonMessageType]#Context, out: Collector[CommonMessageType]) => if (value.valid) {
        out.collect(value)
      } else {
        ctx.output(outputTag, value)
      })
      .name(propsModel.savepointPref + "-process-output-tag-set-CommonMessageType").uid(propsModel.savepointPref + "-process-output-tag-set-CommonMessageType")
  }

  def setSink(mainDataStream: DataStream[CommonMessageType],
              propsModel: InputPropsModel,
              producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
             ): Unit = {

    setMainSink(mainDataStream, propsModel, producerFabric)
    setOutSideSink(mainDataStream, propsModel, producerFabric)
  }

  def setMainSink(mainDataStream: DataStream[CommonMessageType],
                  propsModel: InputPropsModel,
                  producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                 ): DataStreamSink[KafkaDto] = {
    val outputTopicName = propsModel.outputSink.createSinkFunction(producerFabric)
    val out = new AvroPullOut()
    mainDataStream
      .map(propsModel.dlqSink.prometheusMetric[CommonMessageType](propsModel.serviceData))
      .map(out)
      .addSink(outputTopicName)
  }

  def setOutSideSink(mainDataStream: DataStream[CommonMessageType],
                     propsModel: InputPropsModel,
                     producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                    ): DataStreamSink[KafkaDto] = {
    val dlqTopicName = propsModel.dlqSink.createSinkFunction(producerFabric)
    val out = new DlqPullOut()
    mainDataStream
      .getSideOutput(outputTag)
      .map(propsModel.outputSink.prometheusMetric[CommonMessageType](propsModel.serviceData))
      .map(out)
      .addSink(dlqTopicName)
      .name(propsModel.savepointPref + "-sink-outInputConvertorDlq").uid(propsModel.savepointPref + "-sink-outInputConvertorDlq")
  }
}
