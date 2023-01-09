package ru.vtb.uasp.pilot.model.vector

import com.sksamuel.avro4s.{AvroSchema, ScalePrecision}
import org.apache.flink.api.common.serialization.AbstractDeserializationSchema
import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer.Semantic
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}
import org.apache.flink.util.Collector
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.JsObject
import ru.vtb.uasp.common.constants.BigDecimalConst.{PRECISION, SCALE}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.dto.entity.utils.ModelVectorPropsModel
import ru.vtb.uasp.common.kafka.ConsumerFactory
import ru.vtb.uasp.common.service.JsonConvertInService
import ru.vtb.uasp.common.utils.config.ConfigUtils.{getAllProps, getPropsFromResourcesFile}
import ru.vtb.uasp.common.utils.json.JsonConverter.mapFields
import ru.vtb.uasp.pilot.model.vector.constants.ModelVector._
import ru.vtb.uasp.pilot.model.vector.constants.Tags._
import ru.vtb.uasp.pilot.model.vector.dao.kafka.FlinkKafkaSerializationSchema
import ru.vtb.uasp.pilot.model.vector.service._

import java.util.Properties
import scala.util.{Failure, Random, Success}


object UaspStreamingModelVector {
  private val logger: Logger = LoggerFactory.getLogger(getClass.getName)
  private val transactionIdKey = "transactional.id"

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

      env.execute(propsModel.appServiceName)

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

  def initProps(args: Array[String], appPrefix: String = ""): ModelVectorPropsModel = {
    val stringToString = getAllProps(args)
    val appPropsLocal: Map[String, String] = scala.util.Try(getPropsFromResourcesFile("application-local.properties").get) match {
      case Success(x) => if (appPrefix.nonEmpty) x.updated("model-vector.instance.conf.name", appPrefix) else x
      case Failure(_) => Map()
    }

    ModelVectorPropsModel(appPropsLocal ++ stringToString, appPrefix)
  }

  def init(env: StreamExecutionEnvironment, propsModel: ModelVectorPropsModel): DataStream[UaspDto] = {
    val consumer: FlinkKafkaConsumer[Array[Byte]] = ConsumerFactory.getKafkaConsumer(
      propsModel.consumerTopicName, new AbstractDeserializationSchema[Array[Byte]]() {
        override def deserialize(bytes: Array[Byte]): Array[Byte] = bytes
      }, propsModel.commonKafkaProps)

    env
      .addSource(consumer)
      //      .setParallelism(1)
      .map(d => JsonConvertInService.deselialize[UaspDto](d).right.get)
      .name("Convert in map service")
      .uid(propsModel.transactionalId + "_ConvertInMapService")
  }

  def process(stream: DataStream[UaspDto], propsModel: ModelVectorPropsModel): (DataStream[UaspDto], DataStream[UaspDto]) = {

    val keySelector: KeySelector[UaspDto, String] = (in: UaspDto) => in.id

    val qaEnabled = propsModel.enableQaStream
    val preffix = propsModel.prefixQaStream


    implicit val sp: ScalePrecision = ScalePrecision(SCALE, PRECISION)
    val schema = AvroSchema[UaspDto]
    val aggregatorDefault: AggregateMapService = new AggregateMapService()
    val aggregator: AggregateMapService = new AggregateMapService(CASE_29)

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
          // val isCase29 = msg.dataString.getOrElse(CLASSIFICATION, "").contains(CASE_29)
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

  def setSink(streams: (DataStream[UaspDto], DataStream[UaspDto]), propsModel: ModelVectorPropsModel): DataStreamSink[JsObject] = {
    //        setMainSink(streamProcessed, propsModel)
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


  def setQASink(streamProcessed: DataStream[UaspDto], propsModel: ModelVectorPropsModel): DataStreamSink[JsObject] = {
    val kafkaSerializationSchemaQA = new FlinkKafkaSerializationSchema(propsModel.producerQaTopicName)
    val producerQA = new FlinkKafkaProducer(
      propsModel.producerQaTopicName,
      kafkaSerializationSchemaQA,
      setTransactionIdInProperties(propsModel.producerQaTopicName, propsModel.commonKafkaProps),
      Semantic.EXACTLY_ONCE,
      propsModel.kafkaProducerPoolSize)

    streamProcessed
      .getSideOutput(qAOutputTag)
      .map(new JsonConverterService(mapFields))
      .name("jsonConverter")
      .uid(propsModel.transactionalId + "_QA" + "_JsonConverterService")
      .addSink(producerQA)
  }

  def setMainSink(streamProcessed: DataStream[JsObject], propsModel: ModelVectorPropsModel): DataStreamSink[JsObject] = {
    val kafkaSerializationSchema = new FlinkKafkaSerializationSchema(propsModel.producerTopicName)
    val producer = new FlinkKafkaProducer(
      propsModel.producerTopicName,
      kafkaSerializationSchema,
      setTransactionIdInProperties(propsModel.producerTopicName, propsModel.commonKafkaProps),
      Semantic.EXACTLY_ONCE,
      propsModel.kafkaProducerPoolSize)

    streamProcessed
      .addSink(producer)
  }

  //
  //
  def setCase57Sink(streamProcessed: DataStream[UaspDto], propsModel: ModelVectorPropsModel): DataStreamSink[JsObject] = {
    val kafkaSerializationSchemaQA = new FlinkKafkaSerializationSchema(propsModel.producerPensTopicName)
    val producerPens = new FlinkKafkaProducer(
      propsModel.producerPensTopicName,
      kafkaSerializationSchemaQA,
      setTransactionIdInProperties(propsModel.producerPensTopicName, propsModel.commonKafkaProps),
      Semantic.EXACTLY_ONCE,
      propsModel.kafkaProducerPoolSize)

    streamProcessed
      .getSideOutput(case57OutputTag)
      .map(new JsonConverterService(mapFields))
      .name("jsonConverter")
      .uid(propsModel.transactionalId + "_57_case" + "_JsonConverterService")
      .addSink(producerPens)
  }

  def setCase56Sink(streamProcessed: DataStream[UaspDto], propsModel: ModelVectorPropsModel): DataStreamSink[JsObject] = {
    val kafkaSerializationSchemaFS = new FlinkKafkaSerializationSchema(propsModel.producerFSTopicName)
    val producerFS = new FlinkKafkaProducer(
      propsModel.producerFSTopicName,
      kafkaSerializationSchemaFS,
      setTransactionIdInProperties(propsModel.producerFSTopicName, propsModel.commonKafkaProps),
      Semantic.EXACTLY_ONCE,
      propsModel.kafkaProducerPoolSize)
    val flatJsonConverter = new JsonConverterService(mapFields)

    streamProcessed
      .getSideOutput(case56OutputTag)
      .map(flatJsonConverter)
      .name("jsonConverter")
      .uid(propsModel.transactionalId + "_56_case" + "_JsonConverterService")
      .addSink(producerFS)
  }

  def setCase39Sink(streamProcessed: DataStream[UaspDto], propsModel: ModelVectorPropsModel): DataStreamSink[JsObject] = {
    val kafkaSerializationSchemaFS = new FlinkKafkaSerializationSchema(propsModel.producerPosTopicName)
    val producerFS = new FlinkKafkaProducer(
      propsModel.producerPosTopicName,
      kafkaSerializationSchemaFS,
      setTransactionIdInProperties(propsModel.producerPosTopicName, propsModel.commonKafkaProps),
      Semantic.EXACTLY_ONCE,
      propsModel.kafkaProducerPoolSize)
    val flatJsonConverter = new JsonConverterService(mapFields)

    streamProcessed
      .getSideOutput(case39OutputTag)
      .map(flatJsonConverter)
      .name("jsonConverter")
      .uid(propsModel.transactionalId + "_39_case" + "_JsonConverterService")
      .addSink(producerFS)
  }

  def setCase39NewSink(streamProcessed: DataStream[UaspDto], propsModel: ModelVectorPropsModel): DataStreamSink[JsObject] = {
    val kafkaSerializationSchemaFS = new FlinkKafkaSerializationSchema(propsModel.producerPosNewTopicName)
    val producerFS = new FlinkKafkaProducer(
      propsModel.producerPosNewTopicName,
      kafkaSerializationSchemaFS,
      setTransactionIdInProperties(propsModel.producerPosNewTopicName, propsModel.commonKafkaProps),
      Semantic.EXACTLY_ONCE,
      propsModel.kafkaProducerPoolSize)
    val flatJsonConverter = new JsonConverterService(mapFields)

    streamProcessed
      .getSideOutput(case39NewOutputTag)
      .map(flatJsonConverter)
      .name("jsonConverter")
      .uid(propsModel.transactionalId + "_39New_case" + "_JsonConverterService")
      .addSink(producerFS)
  }


  def setCase38Sink(streamProcessed: DataStream[UaspDto], propsModel: ModelVectorPropsModel): DataStreamSink[JsObject] = {
    val kafkaSerializationSchemaFS = new FlinkKafkaSerializationSchema(propsModel.producerNSTopicName)
    val producer = new FlinkKafkaProducer(
      propsModel.producerNSTopicName,
      kafkaSerializationSchemaFS,
      setTransactionIdInProperties(propsModel.producerNSTopicName, propsModel.commonKafkaProps),
      Semantic.EXACTLY_ONCE,
      propsModel.kafkaProducerPoolSize)
    val flatJsonConverter = new JsonConverterService(mapFields)

    streamProcessed
      .getSideOutput(case38OutputTag)
      .map(flatJsonConverter)
      .name("jsonConverter")
      .uid(propsModel.transactionalId + "_38_case" + "_JsonConverterService")
      .addSink(producer)
  }

  def setCase8Sink(streamProcessed: DataStream[UaspDto], propsModel: ModelVectorPropsModel): DataStreamSink[JsObject] = {
    val kafkaSerializationSchemaFS = new FlinkKafkaSerializationSchema(propsModel.producerCase8TopicName)
    val producer = new FlinkKafkaProducer(
      propsModel.producerCase8TopicName,
      kafkaSerializationSchemaFS,
      setTransactionIdInProperties(propsModel.producerCase8TopicName, propsModel.commonKafkaProps),
      Semantic.EXACTLY_ONCE,
      propsModel.kafkaProducerPoolSize)
    val flatJsonConverter = new JsonConverterService(mapFields)

    streamProcessed
      .getSideOutput(case8OutputTag)
      .map(flatJsonConverter)
      .name("jsonConverter")
      .uid(propsModel.transactionalId + "_8_case" + "_JsonConverterService")
      .addSink(producer)
  }

  def setCase29Sink(streamProcessed: DataStream[UaspDto], propsModel: ModelVectorPropsModel): DataStreamSink[JsObject] = {
    val kafkaSerializationSchemaFS = new FlinkKafkaSerializationSchema(propsModel.producerCase29TopicName)
    val producer = new FlinkKafkaProducer(
      propsModel.producerCase29TopicName,
      kafkaSerializationSchemaFS,
      setTransactionIdInProperties(propsModel.producerCase29TopicName, propsModel.commonKafkaProps),
      Semantic.EXACTLY_ONCE,
      propsModel.kafkaProducerPoolSize)
    val flatJsonConverter = new JsonConverterService(mapFields)

    streamProcessed
      //      .name("29CaseAggregate")
      //      .uid(propsModel.transactionalId + "_29_case" + "_AggregateService")
      //      .map(new AggregateMapService())
      .map(flatJsonConverter)
      .name("jsonConverter")
      .uid(propsModel.transactionalId + "_29_case" + "_JsonConverterService")
      .addSink(producer)
  }


  def setCase44Sink(streamProcessed: DataStream[UaspDto], propsModel: ModelVectorPropsModel): DataStreamSink[JsObject] = {
    val kafkaSerializationSchemaFS = new FlinkKafkaSerializationSchema(propsModel.producerCase44TopicName)
    val producer = new FlinkKafkaProducer(
      propsModel.producerCase44TopicName,
      kafkaSerializationSchemaFS,
      setTransactionIdInProperties(propsModel.producerCase44TopicName, propsModel.commonKafkaProps),
      Semantic.EXACTLY_ONCE,
      propsModel.kafkaProducerPoolSize)
    val flatJsonConverter = new JsonConverterService(mapFields)

    streamProcessed
      .getSideOutput(case44OutputTag)
      .map(flatJsonConverter)
      .name("jsonConverter")
      .uid(propsModel.transactionalId + "_44_case" + "_JsonConverterService")
      .addSink(producer)
  }

  def setCase71Sink(streamProcessed: DataStream[UaspDto], propsModel: ModelVectorPropsModel): DataStreamSink[JsObject] = {
    val kafkaSerializationSchemaFS = new FlinkKafkaSerializationSchema(propsModel.producerCase71TopicName)
    val producer = new FlinkKafkaProducer(
      propsModel.producerCase71TopicName,
      kafkaSerializationSchemaFS,
      setTransactionIdInProperties(propsModel.producerCase71TopicName, propsModel.commonKafkaProps),
      Semantic.EXACTLY_ONCE,
      propsModel.kafkaProducerPoolSize)
    val flatJsonConverter = new JsonConverterService(mapFields)

    streamProcessed
      .getSideOutput(case71OutputTag)
      .map(flatJsonConverter)
      .name("jsonConverter")
      .uid(propsModel.transactionalId + "_71_case" + "_JsonConverterService")
      .addSink(producer)
  }

  def setCase51Sink(streamProcessed: DataStream[UaspDto], propsModel: ModelVectorPropsModel): DataStreamSink[JsObject] = {
    val kafkaSerializationSchemaFS = new FlinkKafkaSerializationSchema(propsModel.producerCase51TopicName)
    val producer = new FlinkKafkaProducer(
      propsModel.producerCase51TopicName,
      kafkaSerializationSchemaFS,
      setTransactionIdInProperties(propsModel.producerCase51TopicName, propsModel.commonKafkaProps),
      Semantic.EXACTLY_ONCE,
      propsModel.kafkaProducerPoolSize)
    val flatJsonConverter = new JsonConverterService(mapFields)

    streamProcessed
      .getSideOutput(case51OutputTag)
      .map(flatJsonConverter)
      .name("jsonConverter")
      .uid(propsModel.transactionalId + "_51_case" + "_JsonConverterService")
      .addSink(producer)
  }

  def setCase48Sink(streamProcessed: DataStream[UaspDto], propsModel: ModelVectorPropsModel): DataStreamSink[JsObject] = {
    val kafkaSerializationSchemaFS = new FlinkKafkaSerializationSchema(propsModel.producerCase48TopicName)
    val producer = new FlinkKafkaProducer(
      propsModel.producerCase48TopicName,
      kafkaSerializationSchemaFS,
      setTransactionIdInProperties(propsModel.producerCase48TopicName, propsModel.commonKafkaProps),
      Semantic.EXACTLY_ONCE,
      propsModel.kafkaProducerPoolSize)
    val flatJsonConverter = new JsonConverterService(mapFields)

    streamProcessed
      .getSideOutput(case48OutputTag)
      .map(flatJsonConverter)
      .name("jsonConverter")
      .uid(propsModel.transactionalId + "_48_case" + "_JsonConverterService")
      .addSink(producer)
  }

  def setCase68Sink(streamProcessed: DataStream[UaspDto], propsModel: ModelVectorPropsModel): DataStreamSink[JsObject] = {
    val kafkaSerializationSchemaFS = new FlinkKafkaSerializationSchema(propsModel.producerCase68TopicName)
    val producer = new FlinkKafkaProducer(
      propsModel.producerCase68TopicName,
      kafkaSerializationSchemaFS,
      setTransactionIdInProperties(propsModel.producerCase68TopicName, propsModel.commonKafkaProps),
      Semantic.EXACTLY_ONCE,
      propsModel.kafkaProducerPoolSize)
    val flatJsonConverter = new JsonConverterService(mapFields)

    streamProcessed
      .getSideOutput(case68OutputTag)
      .map(flatJsonConverter)
      .name("jsonConverter")
      .uid(propsModel.transactionalId + "_68_case" + "_JsonConverterService")
      .addSink(producer)
  }

  def setTransactionIdInProperties(topicName: String, props: Properties): Properties = {
    val localKafkaProps = props.clone.asInstanceOf[Properties]
    localKafkaProps.setProperty("transactional.id", topicName + "-id-" + Random.nextInt(999999999).toString)
    localKafkaProps
  }

}
