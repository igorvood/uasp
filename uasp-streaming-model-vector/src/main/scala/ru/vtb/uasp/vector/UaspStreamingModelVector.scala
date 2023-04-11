package ru.vtb.uasp.vector

import org.apache.flink.api.java.functions.KeySelector
import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.datastream.DataStreamSink
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.slf4j.{Logger, LoggerFactory}
import ru.vtb.uasp.common.abstraction.FlinkStreamPredef.{StreamExecutionEnvironmentPredef, createProducerWithMetric}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.kafka.FlinkSinkProperties.producerFactoryDefault
import ru.vtb.uasp.common.service.UaspDeserializationProcessFunction
import ru.vtb.uasp.common.service.dto.KafkaDto
import ru.vtb.uasp.vector.config.CasePropsModel
import ru.vtb.uasp.vector.config.CasePropsModel.appPrefixDefaultName
import ru.vtb.uasp.vector.dsl.RuleParser
import ru.vtb.uasp.vector.service._

import scala.util.{Failure, Success, Try}


object UaspStreamingModelVector {
  private val logger: Logger = LoggerFactory.getLogger(getClass.getName)

  def main(args: Array[String]): Unit = {
    logger.info("Start app" + this.getClass.getName)

    Try {
      val propsModel = CasePropsModel.configApp(appPrefixDefaultName, args)

      val env = StreamExecutionEnvironment.getExecutionEnvironment
      env.setMaxParallelism(propsModel.appSyncParallelism)

/*      env.enableCheckpointing(propsModel.appStreamCheckpointTimeMilliseconds)
      env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
      env.getCheckpointConfig.setMaxConcurrentCheckpoints(propsModel.stateCheckpointsNumRetained)*/

      val stream = init(env, propsModel)
      val streamProcessed = process(stream, propsModel)
      setSinks(streamProcessed, propsModel)

      env.execute(propsModel.appServiceName.fullServiceName)

    } match {
      case Failure(exception) =>
        exception.printStackTrace()
        logger.error("error:" + exception.getMessage)
        System.exit(1)
      case Success(_) =>
    }
  }

  def init(env: StreamExecutionEnvironment, propsModel: CasePropsModel, producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault): DataStream[UaspDto] = {
    val main = env.registerConsumerWithMetric(
      propsModel.appServiceName,
      propsModel.consumer,
      Some(propsModel.dlqProducer),
      UaspDeserializationProcessFunction(),
      producerFabric
    )

    main
  }

  def process(stream: DataStream[UaspDto], propsModel: CasePropsModel): DataStream[KafkaDto] = {
    val keySelector: KeySelector[UaspDto, String] = (in: UaspDto) => in.id

    stream
      .keyBy(keySelector)
      .process(new RuleApplyMapFunction)
  }

  def dlqSink(streamProcessed: DataStream[KafkaDto], propsModel: CasePropsModel, producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault): DataStreamSink[KafkaDto] = {
    createProducerWithMetric(
      streamProcessed
        .getSideOutput(RuleApplyMapFunction.dlqRuleApplyMapFunction),
      propsModel.appServiceName,
      propsModel.dlqProducer,
      producerFabric
    )
  }

  def setSinks(streamProcessed: DataStream[KafkaDto], propsModel: CasePropsModel, producerFabric: FlinkSinkProperties => SinkFunction[KafkaDto] = producerFactoryDefault): Seq[DataStreamSink[KafkaDto]] = {
    dlqSink(streamProcessed, propsModel)

    RuleParser
      .caseRulesMap
      .map {
        case (ruleName, rule) =>
          createProducerWithMetric[KafkaDto](
            streamProcessed
              .getSideOutput(RuleParser.tags(ruleName)),
            propsModel.appServiceName,
            propsModel.caseProps(rule.outputTopicName).right.get,
            producerFabric
          )
      }.toSeq
  }

}
