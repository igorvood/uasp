package ru.vtb.uasp.vector.util

import org.apache.flink.api.common.typeinfo.BasicTypeInfo
import org.apache.flink.streaming.api.operators.KeyedProcessOperator
import org.apache.flink.streaming.util.KeyedOneInputStreamOperatorTestHarness
import play.api.libs.json.JsObject
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.service.dto.KafkaDto
import ru.vtb.uasp.vector.service.RuleApplyMapFunction

object TestUtil {
  def testHarnessCreator(enrichmentMapService: RuleApplyMapFunction, mdm: UaspDto): KeyedOneInputStreamOperatorTestHarness[String, UaspDto, KafkaDto] = {
    val testHarness = new KeyedOneInputStreamOperatorTestHarness(
      new KeyedProcessOperator(enrichmentMapService),
      (in: UaspDto) => in.id, BasicTypeInfo.STRING_TYPE_INFO
    )

    testHarness.open()
    testHarness.processElement(mdm, 10)
    testHarness
  }
}
