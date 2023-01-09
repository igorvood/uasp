package ru.vtb.bevent.first.salary.aggregate

import org.apache.flink.api.common.serialization.AbstractDeserializationSchema
import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.scala.{DataStream, OutputTag, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer.Semantic
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}
import org.slf4j.{Logger, LoggerFactory}
import ru.vtb.bevent.first.salary.aggregate.Util.FlinkKafkaSerializationSchemaUaspJson
import ru.vtb.bevent.first.salary.aggregate.constants.ConfirmedPropsModel
import ru.vtb.bevent.first.salary.aggregate.service.AggregateFirstSalaryRichMapFunction
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.kafka.ConsumerFactory
import ru.vtb.uasp.common.service.JsonConvertInService
import ru.vtb.uasp.common.utils.config.ConfigUtils.getAllProps

import java.util.Properties
import scala.util.Random


object UaspStreamingAggregateFirstSalary {
  private val logger: Logger = LoggerFactory.getLogger(getClass.getName)
  val dlqOutputTag = OutputTag[UaspDto]("dlq")

  val keySlector: KeySelector[UaspDto, String] = new KeySelector[UaspDto, String]() {
    override def getKey(value: UaspDto): String = value.id
  }

  def main(args: Array[String]): Unit = {
    logger.info("Start app" + this.getClass.getName)

    try {
      val env = StreamExecutionEnvironment.getExecutionEnvironment

      val props = initProps(args)
      val dataStream = init(env, props)
      val mainStream = process(dataStream, props)

      setSink(mainStream, props)
      env.execute(props.appServiceName)
    } catch {
      case e: Throwable => e match {
        case _ =>
          logger.error("error:" + e.getMessage)
          e.printStackTrace()
          System.exit(1)
      }
    }
  }

  def initProps(args: Array[String]): ConfirmedPropsModel = {
    ConfirmedPropsModel(getAllProps(args))
  }


  def init(env: StreamExecutionEnvironment, propsModel: ConfirmedPropsModel): DataStream[UaspDto] = {
    env.enableCheckpointing(propsModel.appStreamCheckpointTimeMilliseconds)
    env.getCheckpointConfig.setMaxConcurrentCheckpoints(propsModel.stateCheckpointsNumRetained)
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
    env.setParallelism(propsModel.maxParallelism)
    env.getCheckpointConfig.setCheckpointTimeout(propsModel.streamCheckpointTimeoutMilliseconds)

    initHA(env, propsModel)
  }

  def initHA(env: StreamExecutionEnvironment, propsModel: ConfirmedPropsModel): DataStream[UaspDto] = {

    val consumerHA: FlinkKafkaConsumer[Array[Byte]] = ConsumerFactory.getKafkaConsumer(
      propsModel.topicHA, new AbstractDeserializationSchema[Array[Byte]]() {
        override def deserialize(bytes: Array[Byte]): Array[Byte] = bytes
      }, propsModel.commonKafkaProps)

    consumerHA.setStartFromLatest()

    env
      .addSource(consumerHA)
      .map(d => JsonConvertInService.deselialize[UaspDto](d).right.get)
      .name("HA Convertor")
  }

  def process(dataStream: DataStream[UaspDto], propsModel: ConfirmedPropsModel): DataStream[UaspDto] = {

    dataStream
      .keyBy(keySlector)
      .process(new AggregateFirstSalaryRichMapFunction(propsModel))
  }

  //  private val transactionIdKey = "transactional.id"
  def addRandomTransactionId(topicName: String, props: Properties): Properties = {
    props.setProperty("enable.idempotence", "true")
    props.setProperty("max.in.flight.requests.per.connection", "5")
    props.setProperty("retries", "1")
    props.setProperty("acks", "all")
    props.setProperty("isolation.level", "read_committed")
    props.setProperty("transactional.id", topicName + "-id-" + Random.nextInt(999999999).toString)

    props
  }

  def setOutsideSink(mainDataStream: DataStream[UaspDto], propsModel: ConfirmedPropsModel): DataStreamSink[UaspDto] = {
    val producerHADlq = new FlinkKafkaProducer(
      propsModel.topicDlqName,
      new FlinkKafkaSerializationSchemaUaspJson(propsModel.topicDlqName),
      addRandomTransactionId(propsModel.topicDlqName, propsModel.commonKafkaProps),
      Semantic.EXACTLY_ONCE,
      propsModel.kafkaProducerPoolSize)

    mainDataStream
      .getSideOutput(dlqOutputTag)
      .addSink(producerHADlq)
      .name("DLQ : " + propsModel.topicDlqName)
  }

  def setMainSink(mainDataStream: DataStream[UaspDto], propsModel: ConfirmedPropsModel): DataStreamSink[UaspDto] = {
    val producerCommon = new FlinkKafkaProducer(
      propsModel.topicOutputName,
      new FlinkKafkaSerializationSchemaUaspJson(propsModel.topicOutputName),
      addRandomTransactionId(propsModel.topicOutputName, propsModel.commonKafkaProps),
      Semantic.EXACTLY_ONCE,
      propsModel.kafkaProducerPoolSize
    )

    mainDataStream
      .addSink(producerCommon)
  }

  def setSink(mainDataStream: DataStream[UaspDto], propsModel: ConfirmedPropsModel): DataStreamSink[UaspDto] = {
    setOutsideSink(mainDataStream, propsModel)
    setMainSink(mainDataStream, propsModel)
  }

}
