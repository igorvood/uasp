package ru.vtb.uasp.pilot.model.vector

import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.{DataStream, OutputTag, StreamExecutionEnvironment}
import org.apache.flink.util.Collector
import org.slf4j.{Logger, LoggerFactory}
import ru.vtb.uasp.common.abstraction.FlinkStreamPredef.{StreamExecutionEnvironmentPredef, createProducerWithMetric}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.dto.entity.utils.ModelVectorPropsModel
import ru.vtb.uasp.common.dto.entity.utils.ModelVectorPropsModel.appPrefixDefaultName
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.kafka.FlinkSinkProperties.producerFactoryDefault
import ru.vtb.uasp.common.service.UaspDeserializationProcessFunction
import ru.vtb.uasp.common.service.dto.{KafkaDto, ServiceDataDto}
import ru.vtb.uasp.pilot.model.vector.constants.ModelVector._
import ru.vtb.uasp.pilot.model.vector.constants.Tags._
import ru.vtb.uasp.pilot.model.vector.dao.kafka.KafkaDtoSerializationService
import ru.vtb.uasp.pilot.model.vector.service._


object UaspStreamingModelVector {
  private val logger: Logger = LoggerFactory.getLogger(getClass.getName)

  def main(args: Array[String]): Unit = {
    logger.info("Start app" + this.getClass.getName)

    try {
      val propsModel = initProps(args)


      val env = StreamExecutionEnvironment.getExecutionEnvironment
      env.setMaxParallelism(propsModel.maxParallelism)

      env.enableCheckpointing(propsModel.appStreamCheckpointTimeSeconds)
      env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
      env.getCheckpointConfig.setMaxConcurrentCheckpoints(propsModel.appStateCheckpointsNumRetained)


      val stream = init(env, propsModel)
      val streamProcessed = process(stream, propsModel)
      setSink(streamProcessed, propsModel)

      env.execute(propsModel.appServiceName.fullServiceName)

    } catch {
      case e: Throwable => e match {
        case _ =>
          e.printStackTrace()
          logger.error("Error:" + e.toString)
          logger.error("Error1:" + e.getCause.getMessage)
          logger.error("error:" + e.getMessage)
          System.exit(1)
      }
    }
  }

  def initProps(args: Array[String]): ModelVectorPropsModel =
    ModelVectorPropsModel.configApp(appPrefixDefaultName, args)

  def init(env: StreamExecutionEnvironment,
           propsModel: ModelVectorPropsModel,
           producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
          ): DataStream[UaspDto] = {
    val serialisationProcessFunction = UaspDeserializationProcessFunction()
    env
      .registerConsumerWithMetric(
        propsModel.appServiceName,
        propsModel.consumerTopicName,
        None,
        serialisationProcessFunction,
        producerFabric)
      .name("Convert in map service")
      .uid(propsModel.transactionalId + "_ConvertInMapService")
  }

  def process(stream: DataStream[UaspDto], propsModel: ModelVectorPropsModel): (DataStream[UaspDto], DataStream[UaspDto]) = {

    val keySelector: KeySelector[UaspDto, String] = (in: UaspDto) => in.id

    val qaEnabled = propsModel.enableQaStream
    val preffix = propsModel.prefixQaStream

    val aggregatorDefault: AggregateMapService = new AggregateMapService()

    val streamDefault =
      stream
        .keyBy(keySelector)
        .map(aggregatorDefault)
        .name("aggregator")
        .uid(propsModel.transactionalId + "_aggregator")
        .process((msg: UaspDto, ctx: ProcessFunction[UaspDto, UaspDto]#Context, out: Collector[UaspDto]) => {
          println("Classification : " + msg.dataString.getOrElse(CLASSIFICATION, ""))
          val isQa = qaEnabled && msg.id.startsWith(preffix)
          val isCase8 = msg.dataString.getOrElse(CLASSIFICATION, "").contains(CASE_8)
          val isCase38 = msg.dataString.getOrElse(CLASSIFICATION, "").contains(CASE_38)
          val isCase39 = msg.dataString.getOrElse(CLASSIFICATION, "").contains(CASE_39)
          val isCase39New = msg.dataString.getOrElse(CLASSIFICATION, "").contains(CASE_39_NEW)
          val isCase44 = msg.dataString.getOrElse(CLASSIFICATION, "").contains(CASE_44)
          val isCase56 = msg.dataString.getOrElse(CLASSIFICATION, "").contains(CASE_56)
          val isCase57 = msg.dataString.getOrElse(CLASSIFICATION, "").contains(CASE_57)
          val isCase71 = msg.dataString.getOrElse(CLASSIFICATION, "").contains(CASE_71)
          val isCase51 = msg.dataString.getOrElse(CLASSIFICATION, "").contains(CASE_51)
          val isCase48 = msg.dataString.getOrElse(CLASSIFICATION, "").contains(CASE_48)
          val isCase68 = msg.dataString.getOrElse(CLASSIFICATION, "").contains(CASE_68)

          if (isCase8 && !isQa) {
            ctx.output(case8OutputTag, msg)
          }
          else if (isCase38 && !isQa) {
            ctx.output(case38OutputTag, msg)
          }
          else if (isCase39 && !isQa) {
            ctx.output(case39OutputTag, msg)
          }
          else if (isCase39New && !isQa) {
            ctx.output(case39NewOutputTag, msg)
          }
          else if (isCase44 && !isQa) {
            ctx.output(case44OutputTag, msg)
          }
          else if (isCase56 && !isQa) {
            ctx.output(case56OutputTag, msg)
          }
          else if (isCase71 && !isQa) {
            ctx.output(case71OutputTag, msg)
          }
          else if (isCase51 && !isQa) {
            ctx.output(case51OutputTag, msg)
          }
          else if (isCase48 && !isQa) {
            ctx.output(case48OutputTag, msg)
          }
          else if (isCase68 && !isQa) {
            ctx.output(case68OutputTag, msg)
          }
          else if (isCase57 && !isQa) {
            ctx.output(case57OutputTag, msg)
          }
          else if (isQa) {
            ctx.output(qAOutputTag, msg)
          } else {
            out.collect(msg)
          }
        })

    //кейс 29 пересекается с некоторыми кейсами выше, поэтому нужно 2 сообщения выдавать
    val streamCase29 =
      stream
        .keyBy(keySelector)
        .filter(uasp => uasp.dataString.getOrElse(CLASSIFICATION, "").contains(CASE_29))
        .map(new AggregateMapService(CASE_29))
        .name("aggregate_map_service_29")
        .uid(propsModel.transactionalId + "aggregate_map_service_29")

    (streamDefault, streamCase29)

  }

  def setSink(streams: (DataStream[UaspDto], DataStream[UaspDto]), propsModel: ModelVectorPropsModel): DataStreamSink[KafkaDto] = {
    setCase56Sink(streams._1, propsModel)
    setCase39Sink(streams._1, propsModel)
    setCase39NewSink(streams._1, propsModel)
    setCase57Sink(streams._1, propsModel)
    setCase38Sink(streams._1, propsModel)
    setCase8Sink(streams._1, propsModel)
    setCase29Sink(streams._2, propsModel)
    setCase44Sink(streams._1, propsModel)
    setCase71Sink(streams._1, propsModel)
    setCase51Sink(streams._1, propsModel)
    setCase48Sink(streams._1, propsModel)
    setCase68Sink(streams._1, propsModel)
    setQASink(streams._1, propsModel)
  }


  def setQASink(streamProcessed: DataStream[UaspDto],
                propsModel: ModelVectorPropsModel,
                producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
               ): DataStreamSink[KafkaDto] = {
    setCaseSink(streamProcessed,
      propsModel.transactionalId,
      producerFabric,
      "QA",
      Some(qAOutputTag),
      propsModel.producerQaTopicName,
      propsModel.flatJsonConverter,
      propsModel.kafkaDtoSerializationService,
      propsModel.appServiceName)
  }

  def setCase57Sink(streamProcessed: DataStream[UaspDto],
                    propsModel: ModelVectorPropsModel,
                    producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                   ): DataStreamSink[KafkaDto] = {
    setCaseSink(streamProcessed,
      propsModel.transactionalId,
      producerFabric,
      "57_case",
      Some(case57OutputTag),
      propsModel.producerPensTopicName,
      propsModel.flatJsonConverter,
      propsModel.kafkaDtoSerializationService,
      propsModel.appServiceName
    )

  }

  def setCase56Sink(streamProcessed: DataStream[UaspDto],
                    propsModel: ModelVectorPropsModel,
                    producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                   ): DataStreamSink[KafkaDto] = {
    setCaseSink(streamProcessed,
      propsModel.transactionalId,
      producerFabric,
      "56_case",
      Some(case56OutputTag),
      propsModel.producerFSTopicName,
      propsModel.flatJsonConverter,
      propsModel.kafkaDtoSerializationService,
      propsModel.appServiceName
    )
  }

  def setCase39Sink(streamProcessed: DataStream[UaspDto],
                    propsModel: ModelVectorPropsModel,
                    producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                   ): DataStreamSink[KafkaDto] = {

    setCaseSink(streamProcessed,
      propsModel.transactionalId,
      producerFabric,
      "39_case",
      Some(case39OutputTag),
      propsModel.producerPosTopicName,
      propsModel.flatJsonConverter,
      propsModel.kafkaDtoSerializationService,
      propsModel.appServiceName)

  }

  def setCase39NewSink(streamProcessed: DataStream[UaspDto],
                       propsModel: ModelVectorPropsModel,
                       producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                      ): DataStreamSink[KafkaDto] = {

    setCaseSink(streamProcessed,
      propsModel.transactionalId,
      producerFabric,
      "39New_case",
      Some(case39NewOutputTag),
      propsModel.producerPosNewTopicName,
      propsModel.flatJsonConverter,
      propsModel.kafkaDtoSerializationService,
      propsModel.appServiceName)

  }


  def setCase38Sink(streamProcessed: DataStream[UaspDto],
                    propsModel: ModelVectorPropsModel,
                    producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                   ): DataStreamSink[KafkaDto] = {
    setCaseSink(streamProcessed,
      propsModel.transactionalId,
      producerFabric,
      "38_case",
      Some(case38OutputTag),
      propsModel.producerNSTopicName,
      propsModel.flatJsonConverter,
      propsModel.kafkaDtoSerializationService,
      propsModel.appServiceName)

  }

  def setCase8Sink(streamProcessed: DataStream[UaspDto],
                   propsModel: ModelVectorPropsModel,
                   producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                  ): DataStreamSink[KafkaDto] = {
    setCaseSink(streamProcessed,
      propsModel.transactionalId,
      producerFabric,
      "8_case",
      Some(case8OutputTag),
      propsModel.producerCase8TopicName,
      propsModel.flatJsonConverter,
      propsModel.kafkaDtoSerializationService,
      propsModel.appServiceName)

  }

  def setCase29Sink(streamProcessed: DataStream[UaspDto],
                    propsModel: ModelVectorPropsModel,
                    producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                   ): DataStreamSink[KafkaDto] = {


    setCaseSink(streamProcessed,
      propsModel.transactionalId,
      producerFabric,
      "29_case",
      None,
      propsModel.producerCase29TopicName,
      propsModel.flatJsonConverter,
      propsModel.kafkaDtoSerializationService,
      propsModel.appServiceName)
  }

  def setCase44Sink(streamProcessed: DataStream[UaspDto],
                    propsModel: ModelVectorPropsModel,
                    producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                   ): DataStreamSink[KafkaDto] = {
    setCaseSink(streamProcessed,
      propsModel.transactionalId,
      producerFabric,
      "44_case",
      Some(case44OutputTag),
      propsModel.producerCase44TopicName,
      propsModel.flatJsonConverter,
      propsModel.kafkaDtoSerializationService,
      propsModel.appServiceName)

  }

  def setCase71Sink(streamProcessed: DataStream[UaspDto],
                    propsModel: ModelVectorPropsModel,
                    producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                   ): DataStreamSink[KafkaDto] = {

    setCaseSink(streamProcessed,
      propsModel.transactionalId,
      producerFabric,
      "71_case",
      Some(case71OutputTag),
      propsModel.producerCase71TopicName,
      propsModel.flatJsonConverter,
      propsModel.kafkaDtoSerializationService,
      propsModel.appServiceName)

  }

  def setCase51Sink(streamProcessed: DataStream[UaspDto],
                    propsModel: ModelVectorPropsModel,
                    producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                   ): DataStreamSink[KafkaDto] = {

    setCaseSink(streamProcessed,
      propsModel.transactionalId,
      producerFabric,
      "51_case",
      Some(case51OutputTag),
      propsModel.producerCase51TopicName,
      propsModel.flatJsonConverter,
      propsModel.kafkaDtoSerializationService,
      propsModel.appServiceName)

  }

  def setCase48Sink(streamProcessed: DataStream[UaspDto],
                    propsModel: ModelVectorPropsModel,
                    producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                   ): DataStreamSink[KafkaDto] = {
    setCaseSink(streamProcessed,
      propsModel.transactionalId,
      producerFabric,
      "48_case",
      Some(case48OutputTag),
      propsModel.producerCase48TopicName,
      propsModel.flatJsonConverter,
      propsModel.kafkaDtoSerializationService,
      propsModel.appServiceName)

  }

  def setCase68Sink(streamProcessed: DataStream[UaspDto],
                    propsModel: ModelVectorPropsModel,
                    producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                   ): DataStreamSink[KafkaDto] = {

    setCaseSink(streamProcessed,
      propsModel.transactionalId,
      producerFabric,
      "68_case",
      Some(case68OutputTag),
      propsModel.producerCase68TopicName,
      propsModel.flatJsonConverter,
      propsModel.kafkaDtoSerializationService,
      propsModel.appServiceName
    )

  }

  private def setCaseSink(streamProcessed: DataStream[UaspDto],
                          transactionalId: String,
                          producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto],
                          caseName: String,
                          outputTag: Option[OutputTag[UaspDto]],
                          flinkSinkProperties: FlinkSinkProperties,
                          flatJsonConverter: JsonConverterService,
                          kafkaDtoSerializationService: KafkaDtoSerializationService,
                          serviceData: ServiceDataDto,
                         ): DataStreamSink[KafkaDto] = {

    val stream: DataStream[KafkaDto] = outputTag
      .map(tag =>
        streamProcessed
          .getSideOutput(tag)
      ).getOrElse(streamProcessed)
      .map(flatJsonConverter)
      .name("jsonConverter")
      .uid(s"${transactionalId}_${caseName}_JsonConverterService")
      .map(kafkaDtoSerializationService)


    createProducerWithMetric(stream, serviceData, flinkSinkProperties, producerFabric)


  }

}
