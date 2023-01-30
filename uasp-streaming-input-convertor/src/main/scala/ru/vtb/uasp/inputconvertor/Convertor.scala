package ru.vtb.uasp.inputconvertor

import com.sksamuel.avro4s.AvroSchema
import org.apache.avro.Schema
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer.Semantic
import org.apache.flink.util.Collector
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.config.ConfigUtils.{getAllProps, getPropsFromResourcesFile}
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.service.ConvertHelper.validAndTransform
import ru.vtb.uasp.inputconvertor.service._
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel
import ru.vtb.uasp.inputconvertor.utils.serialization.{AvroPullOut, DLQSerializer, InputMessageTypeDeserialization}
import ru.vtb.uasp.validate.DroolsValidator

import scala.util.{Failure, Success}

object Convertor {

  private val producerSemantic = Semantic.AT_LEAST_ONCE
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

      env.execute(propsModel.appServiceName)
    } catch {
      case e: Exception =>
        e.printStackTrace()
        //logger.error("Error:" + e.getMessage)
        System.exit(1)
    }
  }

  def initProps(args: Array[String], appPrefix: String = ""): InputPropsModel = {
    val stringToString = getAllProps(args)
    val appPropsLocal: Map[String, String] = scala.util.Try(getPropsFromResourcesFile("application-local.properties").get) match {
      case Success(x) => if (appPrefix.nonEmpty) x.updated("input-convertor.instance.conf.name", appPrefix) else x
      case Failure(_) => Map()
    }

    InputPropsModel(appPropsLocal ++ stringToString, appPrefix)
  }

  def init(env: StreamExecutionEnvironment, propsModel: InputPropsModel) = {


    val readSourceTopicFromBeginning: Boolean = propsModel.appReadSourceTopicFrombeginning.toUpperCase == "Y"

    val messageInputStream: DataStream[InputMessageType] = env.addSource(
      KafkaConsumerService.getKafkaConsumer[InputMessageType](propsModel.appInputTopicName,
        propsModel.commonKafkaProps, new InputMessageTypeDeserialization(),
        propsModel.appInputTopicGroupId, readSourceTopicFromBeginning)).rebalance
    messageInputStream
  }

  def process(
               messageInputStream: DataStream[InputMessageType],
               propsModel: InputPropsModel): DataStream[CommonMessageType] = {
    val droolsValidator = new DroolsValidator(propsModel.appUaspdtoType + "-validation-rules.drl")
    val avroSchema: Schema = AvroSchema[UaspDto]
    val messageParserFlatMap = new MessageParserFlatMap(propsModel.config)

    val extractJsonStream: DataStream[CommonMessageType] =
      messageInputStream
        .flatMap(messageParserFlatMap)
        .name(propsModel.appSavepointPref + "-flatMap-messageInputStream").uid(propsModel.appSavepointPref + "-flatMap-messageInputStream")
    val convertOutMapService = new ConvertOutMapService

    val commonStream = extractJsonStream
      //TODO: avro schema inference only once
      .map(m => validAndTransform(m, propsModel, propsModel.appUseAvroSerializationIsY,
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
              propsModel: InputPropsModel): Unit = {

    setMainSink(mainDataStream, propsModel)
    setOutSideSink(mainDataStream, propsModel)
  }

  def setMainSink(mainDataStream: DataStream[CommonMessageType],
                  propsModel: InputPropsModel): DataStreamSink[CommonMessageType] = {
    val outputTopicName = propsModel.appOutputTopicName
    mainDataStream
      .addSink(KafkaProducerService.getKafkaProducer[CommonMessageType](
        outputTopicName, propsModel.commonKafkaProps, new AvroPullOut(outputTopicName),
        propsModel.appServiceName + "-" + outputTopicName, producerSemantic, propsModel.kafkaProducerPoolSize))
      .name(propsModel.appSavepointPref + "-sink-outInputConvertor").uid(propsModel.appSavepointPref + "-sink-outInputConvertor")
  }

  def setOutSideSink(mainDataStream: DataStream[CommonMessageType],
                     propsModel: InputPropsModel): DataStreamSink[CommonMessageType] = {
    val dlqTopicName = propsModel.appDlqTopicName
    mainDataStream
      .getSideOutput(outputTag)
      .addSink(KafkaProducerService.getKafkaProducer[CommonMessageType](
        dlqTopicName, propsModel.commonKafkaProps, new DLQSerializer(dlqTopicName),
        propsModel.appServiceName + "-" + dlqTopicName, producerSemantic, propsModel.kafkaProducerPoolSize))
      .name(propsModel.appSavepointPref + "-sink-outInputConvertorDlq").uid(propsModel.appSavepointPref + "-sink-outInputConvertorDlq")
  }
}
