package ru.vtb.bevent.first.salary.aggregate

import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.{DataStream, OutputTag, StreamExecutionEnvironment}
import org.slf4j.{Logger, LoggerFactory}
import ru.vtb.bevent.first.salary.aggregate.constants.ConfirmedPropsModel
import ru.vtb.bevent.first.salary.aggregate.constants.ConfirmedPropsModel.appPrefixDefaultName
import ru.vtb.uasp.common.abstraction.FlinkStreamPredef.StreamExecutionEnvironmentPredef
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.kafka.FlinkSinkProperties.producerFactoryDefault
import ru.vtb.uasp.common.service.JsonConvertOutService.IdentityPredef
import ru.vtb.uasp.common.service.UaspDeserializationProcessFunction
import ru.vtb.uasp.common.service.dto.KafkaDto

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

      val props = initProps(args)

      val env = StreamExecutionEnvironment.getExecutionEnvironment

      val dataStream = init(env, props)
      val mainStream = process(dataStream, props)

      setSink(mainStream, props)
      env.execute(props.appServiceName.fullServiceName)
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
    ConfirmedPropsModel.configApp(appPrefixDefaultName, args)
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

    env
      .registerConsumerWithMetric(
        propsModel.appServiceName,
        propsModel.topicHA,
        Some(propsModel.topicDlqName),
        UaspDeserializationProcessFunction(),
        producerFactoryDefault
      )
      .name("HA Convertor")
  }

  def process(dataStream: DataStream[UaspDto], propsModel: ConfirmedPropsModel): DataStream[UaspDto] = {

    dataStream
      .keyBy(keySlector)
      .process(propsModel.aggregateFirstSalaryRichMapFunction)
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

  def setOutsideSink(mainDataStream: DataStream[UaspDto],
                     propsModel: ConfirmedPropsModel,
                     producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                    ): DataStreamSink[KafkaDto] = {

    val producerHADlq = propsModel.topicDlqName.createSinkFunction(producerFactory)


    mainDataStream
      .getSideOutput(dlqOutputTag)
      .map(d => d.serializeToBytes)
      .addSink(producerHADlq)
      .name("DLQ : " + propsModel.topicDlqName)
  }

  def setMainSink(mainDataStream: DataStream[UaspDto],
                  propsModel: ConfirmedPropsModel,
                  producerFactory: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault
                 ): DataStreamSink[KafkaDto] = {
    val producerCommon = propsModel.topicOutputName.createSinkFunction(producerFactory)

    mainDataStream
      .map(d => d.serializeToBytes)
      .addSink(producerCommon)
  }

  def setSink(mainDataStream: DataStream[UaspDto], propsModel: ConfirmedPropsModel): DataStreamSink[KafkaDto] = {
    setOutsideSink(mainDataStream, propsModel)
    setMainSink(mainDataStream, propsModel)
  }

}
