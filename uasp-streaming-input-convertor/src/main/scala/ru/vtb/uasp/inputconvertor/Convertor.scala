package ru.vtb.uasp.inputconvertor

import com.sksamuel.avro4s.AvroSchema
import org.apache.avro.Schema
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.util.Collector
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.kafka.FlinkSinkProperties.producerFactoryDefault
import ru.vtb.uasp.common.service.dto.KafkaDto
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.service.ConvertHelper.validAndTransform
import ru.vtb.uasp.inputconvertor.service._
import ru.vtb.uasp.inputconvertor.utils.config.NewInputPropsModel
import ru.vtb.uasp.inputconvertor.utils.config.NewInputPropsModel.appPrefixDefaultName
import ru.vtb.uasp.inputconvertor.utils.serialization.NewAvroPullOut
import ru.vtb.uasp.validate.DroolsValidator

object Convertor {

  private val outputTag = OutputTag[CommonMessageType]("dlq")

  def main(args: Array[String]): Unit = {
    try {
      // Initialization

      val propsModel = initProps(args) // доделать рефакторинг, внедрить модель параметров

      val env = StreamExecutionEnvironment.getExecutionEnvironment
      //FIXME disable checkpointing
      //env.enableCheckpointing(propsModel.appStreamCheckpointTimeMilliseconds.toLong, CheckpointingMode.EXACTLY_ONCE)
      // End of Initialization
      val messageInputStream = init(env, propsModel)
      // extract to json
      val mainDataStream = process(messageInputStream, propsModel)
      // get result
      setSink(mainDataStream, propsModel)

      env.execute(propsModel.appServiceName.fullServiceName)
    } catch {
      case e: Exception =>
        e.printStackTrace()
        //logger.error("Error:" + e.getMessage)
        System.exit(1)
    }
  }

  def initProps(args: Array[String]): NewInputPropsModel = {
    //    val stringToString = getAllProps(args)
    //    val appPropsLocal: Map[String, String] = scala.util.Try(getPropsFromResourcesFile("application-local.properties").get) match {
    //      case Success(x) => if (appPrefix.nonEmpty) x.updated("input-convertor.instance.conf.name", appPrefix) else x
    //      case Failure(_) => Map()
    //    }
    //
    //    InputPropsModel(appPropsLocal ++ stringToString, appPrefix)
    NewInputPropsModel.configApp(appPrefixDefaultName, args)
  }

  def init(env: StreamExecutionEnvironment, propsModel: NewInputPropsModel) = {

    val consumer = propsModel.appInputTopicName.createConsumer(propsModel.inputMessageTypeDeserialization)


    val messageInputStream: DataStream[InputMessageType] = env
      .addSource(consumer).rebalance
    messageInputStream
  }

  def process(
               messageInputStream: DataStream[InputMessageType],
               propsModel: NewInputPropsModel): DataStream[CommonMessageType] = {
    val droolsValidator = new DroolsValidator(propsModel.appUaspdtoType + "-validation-rules.drl")
    val avroSchema: Schema = AvroSchema[UaspDto]
    val messageParserFlatMap = new MessageParserFlatMap(propsModel)

    val extractJsonStream: DataStream[CommonMessageType] =
      messageInputStream
        .flatMap(messageParserFlatMap)
        .name(propsModel.appSavepointPref + "-flatMap-messageInputStream").uid(propsModel.appSavepointPref + "-flatMap-messageInputStream")
    val convertOutMapService = new ConvertOutMapService

    val commonStream = extractJsonStream
      //TODO: avro schema inference only once
      .map(m => validAndTransform(m, propsModel, propsModel.appUseAvroSerialization,
        droolsValidator, avroSchema, propsModel.dtoMap, convertOutMapService))
      .name(propsModel.appSavepointPref + "-map-validAndTransform").uid(propsModel.appSavepointPref + "-map-validAndTransform")

    //split valid and invalid messages
    commonStream
      .process((value: CommonMessageType, ctx: ProcessFunction[CommonMessageType,
        CommonMessageType]#Context, out: Collector[CommonMessageType]) => if (value.valid) {
        out.collect(value)
      } else {
        ctx.output(outputTag, value)
      })
      .name(propsModel.appSavepointPref + "-process-output-tag-set-CommonMessageType").uid(propsModel.appSavepointPref + "-process-output-tag-set-CommonMessageType")
  }

  def setSink(mainDataStream: DataStream[CommonMessageType],
              propsModel: NewInputPropsModel,
              producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
             ): Unit = {

    setMainSink(mainDataStream, propsModel, producerFabric)
    setOutSideSink(mainDataStream, propsModel, producerFabric)
  }

  def setMainSink(mainDataStream: DataStream[CommonMessageType],
                  propsModel: NewInputPropsModel,
                  producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                 ): DataStreamSink[KafkaDto] = {
    val outputTopicName = propsModel.appOutputTopicName.createSinkFunction(producerFabric)
    val out = new NewAvroPullOut()
    mainDataStream
      .map(out)
      .addSink(outputTopicName)
  }

  def setOutSideSink(mainDataStream: DataStream[CommonMessageType],
                     propsModel: NewInputPropsModel,
                     producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                    ): DataStreamSink[KafkaDto] = {
    val dlqTopicName = propsModel.appDlqTopicName.createSinkFunction(producerFabric)
    val out = new NewAvroPullOut()
    mainDataStream
      .getSideOutput(outputTag)

      .map(out)
      .addSink(dlqTopicName)
      .name(propsModel.appSavepointPref + "-sink-outInputConvertorDlq").uid(propsModel.appSavepointPref + "-sink-outInputConvertorDlq")
  }
}
