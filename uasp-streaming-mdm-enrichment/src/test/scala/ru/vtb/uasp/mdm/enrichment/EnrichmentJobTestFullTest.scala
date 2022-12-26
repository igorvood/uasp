package ru.vtb.uasp.mdm.enrichment

import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, createTypeInformation}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{JsValue, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.service.JsonConvertInService
import ru.vtb.uasp.common.service.JsonConvertOutService.JsonPredef
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors}
import ru.vtb.uasp.common.utils.config.kafka.KafkaPrdProperty
import ru.vtb.uasp.mdm.enrichment.TestConst._
import ru.vtb.uasp.mdm.enrichment.service.dto.FlinkDataStreams
import ru.vtb.uasp.mdm.enrichment.utils.config._
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.AllEnrichProperty

import java.util
import java.util.{Collections, Properties}
import scala.collection.JavaConverters.mapAsScalaMapConverter

class EnrichmentJobTestFullTest extends AnyFlatSpec with Matchers {


  val sinksMap: Map[String, CollectByteSink] =
    List("dlq__TOPIC",
      "ToGlobal Topic",
      "ToGHypotec Topic",
      "ToRate Topic",
    )
      .map(v => v -> CollectByteSink(v)).toMap
  private val emptyUasp = new UaspDto("uasp id", Map(), Map(), Map(), Map(), Map(), Map(), Map(), "uuid", 1L)


  it should "send all type msg" in {

    CollectByteSink.values.clear()

    val allEnrichProperty = enrichPropertyMap

    val model = MDMEnrichmentPropsModel("", "", allEnrichProperty, 1)

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val mainStream = env.fromCollection(List(emptyUasp))

    val hypothecInputDataStream = env.fromCollection(List(Json.toJson(emptyUasp)))
    val globalDataStream = env.fromCollection(List(Json.toJson(emptyUasp)))

    val streams = FlinkDataStreams(mainStream, Some(hypothecInputDataStream), Some(globalDataStream))

    val value1 = EnrichmentJob.process(streams, model, { pm => CollectByteSink(pm.toTopicName) })

    value1.mainStream.print()

    env.execute("executionEnvironmentProperty.appServiceName")

  }



  it should " fill error if not found global id" in {
    CollectByteSink.values.clear()

    val allEnrichProperty = enrichPropertyMap

    val model = MDMEnrichmentPropsModel("", "", allEnrichProperty, 1)

    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val dto = emptyUasp
    val mainStream = env.fromCollection(List(dto))
    val emptyStream = env.fromCollection(List[JsValue]())
    val streams = FlinkDataStreams(mainStream, Some(emptyStream), Some(emptyStream))

    val value1 = EnrichmentJob.process(streams, model, { pm => CollectByteSink(pm.toTopicName) }).mainStream

    env.execute("executionEnvironmentProperty.appServiceName")

    val uaspDtoList = CollectByteSink.values.asScala

    assertResult(1)(uaspDtoList.size)

    val errUaspList = uaspDtoList.head._2

    assertResult(1)(errUaspList.size)

    val outDtoWithErrors = JsonConvertInService.deserialize[OutDtoWithErrors](errUaspList.get(0).value).right.get

    assertResult(OutDtoWithErrors(dto.serializeToStr, List("Not found global id in state for id = uasp id")))(outDtoWithErrors)

    value1.print()

  }


}


object CollectByteSink {
  val values: java.util.Map[String, java.util.ArrayList[KafkaDto]] = Collections.synchronizedMap(new util.HashMap[String, java.util.ArrayList[KafkaDto]]())

}

case class CollectByteSink(sinkName: String) extends SinkFunction[KafkaDto] {
  override def invoke(value: KafkaDto, context: SinkFunction.Context): Unit = {
    val tuples = CollectByteSink.values.getOrDefault(sinkName, new java.util.ArrayList[KafkaDto]())
    tuples.add(value)
    CollectByteSink.values.put(sinkName, tuples)
  }
}