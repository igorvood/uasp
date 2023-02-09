package ru.vtb.uasp.common.abstraction

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import ru.vtb.uasp.common.abstraction.MiniPipeLineTrait.valuesTestDataDto
import ru.vtb.uasp.common.kafka.FlinkSinkProperties

import java.util
import java.util.Collections
import scala.collection.JavaConverters.iterableAsScalaIterableConverter

trait MiniPipeLineTrait extends Serializable {

  protected def producerFactory[OUT]: FlinkSinkProperties => SinkFunction[OUT] = { fp => CollectByteSink[OUT](fp) }

  private def prodPrint: FlinkSinkProperties => SinkFunction[TestDataDto] = { fp => CollectByteSink(fp) }

  def topicDataArray[OUT](fp: FlinkSinkProperties) = valuesTestDataDto.entrySet()
    .asScala
    .filter { a => a.getKey == fp.toTopicName }
    .map { d => {
      val value = d.getValue.asScala.map(e => e.asInstanceOf[OUT])
      value
    }.head

    }.toList

  def pipeRun[IN: TypeInformation](inData: List[IN],
                                   flinkPipe: DataStream[IN] => Unit
                                  ) = {
    valuesTestDataDto.clear()
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val dataStream = env.fromCollection[IN](inData)

    flinkPipe(dataStream)

    env.execute("executionEnvironmentProperty.appServiceName")

  }


}

object MiniPipeLineTrait {
  val valuesTestDataDto: java.util.Map[String, java.util.ArrayList[Any]] = Collections.synchronizedMap(new util.HashMap[String, java.util.ArrayList[Any]]())
}

private case class CollectByteSink[OUT](sinkName: FlinkSinkProperties) extends SinkFunction[OUT] {


  override def invoke(value: OUT): Unit = invoke(value, null)

  override def invoke(value: OUT, context: SinkFunction.Context): Unit = {
    val tuples = valuesTestDataDto.getOrDefault(sinkName.toTopicName, new java.util.ArrayList[Any]())
    tuples.add(value)
    valuesTestDataDto.put(sinkName.toTopicName, tuples)
  }
}
