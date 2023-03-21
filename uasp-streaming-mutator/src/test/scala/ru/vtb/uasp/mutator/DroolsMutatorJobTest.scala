package ru.vtb.uasp.mutator

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.scalatest.flatspec.AnyFlatSpec
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.mutator.DroolsMutatorJobTestConfiguration.uaspDtoWithFilelds
import ru.vtb.uasp.mutator.ValidUaspDtoGenerator.serviceDataDto
import ru.vtb.uasp.mutator.configuration.drools.KieBaseService
import ru.vtb.uasp.mutator.service.BusinessRulesService
import ru.vtb.uasp.mutator.service.drools.DroolsRunner
import ru.vtb.uasp.mutator.service.dto.UaspOperation

import java.util.Collections

class DroolsMutatorJobTest extends AnyFlatSpec {

  behavior of "DroolsMutatorJobTest"

  it should "main" in {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val preparedStream = env.fromCollection(uaspDtoWithFilelds)

    val validator = new KieBaseService(List("way4-case-2_2.drl", "way4-case-2_3.drl",
      "way4-case-2_4.drl", "way4-case-2_10.drl", "way4-case-5_2.drl", "way4-case-5_3.drl", "way4-case-11_2.drl"))

    val value = preparedStream
      .process(new BusinessRulesService(serviceDataDto, { q =>
        DroolsRunner(validator.kBase)
          .apply(q, { case x: UaspOperation => x })
      }))
    //    value.print()
    value.addSink(CollectSink())

    env.execute("executionEnvironmentProperty.appServiceName")

    val uaspDtoList = CollectSink.values
    assert(!uaspDtoList.get(0).dataInt.contains("someInt2"))
  }
}

object CollectSink {
  val values: java.util.List[UaspDto] = Collections.synchronizedList(new java.util.ArrayList[UaspDto]())
}

case class CollectSink() extends SinkFunction[UaspDto] {
  override def invoke(value: UaspDto, context: SinkFunction.Context): Unit = {
    CollectSink.values.add(value)
  }
}
