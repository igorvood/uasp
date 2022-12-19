package ru.vtb.uasp.mdm.enrichment.service

import org.apache.flink.api.scala.typeutils.Types
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction
import org.apache.flink.streaming.api.operators.co.KeyedCoProcessOperator
import org.apache.flink.streaming.util.KeyedTwoInputStreamOperatorTestHarness
import org.scalatest.flatspec.AnyFlatSpec
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.mdm.enrichment.EnrichmentJob.{keySelectorCa, keySelectorMain}
import ru.vtb.uasp.mdm.enrichment.TestConst._
import ru.vtb.uasp.mdm.enrichment.service.KeyEnrichmentGlobalIdServiceTest._
import ru.vtb.uasp.mdm.enrichment.service.dto.{KeyedCAData, KeyedUasp}

class KeyEnrichmentGlobalIdServiceTest extends AnyFlatSpec {

  behavior of "KeyEnrichmentGlobalIdServiceTest"

  it should " set state is ok" in {
    val globalIdEnrichProperty = enrichPropertyMap.globalIdEnrichProperty.get
    val enrichmentMapService = new KeyGlobalIdEnrichmentMapService(
      globalIdEnrichProperty,
      savepointPref)

    val stringKey = "StringKey"
    val someStrValue = "Какое-то значение"
    val newMsg = getMdmUaspDto.copy(dataString = getMdmUaspDto.dataString + (stringKey -> someStrValue))

    val testHarness = testHarnessCreator(enrichmentMapService)

    val keyedCAData = globalIdEnrichProperty.validateFieldsAndExtractData(newMsg).right.get
    testHarness.processElement2(keyedCAData, 0)

    val stateValue = enrichmentMapService.getRuntimeContext.getState(enrichmentMapService.crossLinkMdmDataStateDescriptor).value()

    val mapStateValue = enrichmentMapService.getRuntimeContext.getState(enrichmentMapService.mapStateDescriptor).value()

    assertResult(global_id_from_Mdm)(stateValue)

    assertResult(1)(mapStateValue.size)
    assertResult(someStrValue)(mapStateValue(stringKey))

    val uaspDtoMain = getMdmUaspDto
    val keyedUasp = KeyedUasp(uaspDtoMain.id, uaspDtoMain)
    testHarness.processElement1(keyedUasp, 20)

    val stateValueWay4 = enrichmentMapService.getRuntimeContext.getState(enrichmentMapService.crossLinkMdmDataStateDescriptor).value()

    assertResult(global_id_from_Mdm)(stateValueWay4)

    val actualResult = testHarness.extractOutputStreamRecords().get(0).getValue

    assert(actualResult.isRight)

    val actualWay4 = actualResult.right.get

    assertResult(getWay4UaspDto.copy(
      id = global_id_from_Mdm,
      dataString = Map("global_id" -> global_id_from_Mdm, "StringValue" -> someStrValue),
      process_timestamp = actualWay4.process_timestamp
    )
    )(actualWay4)

    assertResult("global_id_from_Mdm")(actualWay4.id)

  }

}

object KeyEnrichmentGlobalIdServiceTest {

  private val savepointPref = "savepointPref"


  def getWay4UaspDto: UaspDto = {
    val random = new scala.util.Random
    UaspDto(
      "LocalId1",
      Map(),
      Map(),
      Map(),
      Map(),
      Map(),
      Map(),
      Map(),
      "Mdm_id",
      12
    )
  }

  def getMdmUaspDto: UaspDto = {

    UaspDto(
      "LocalId1",
      Map(),
      Map(),
      Map(),
      Map(),
      Map(),
      Map("global_id" -> global_id_from_Mdm),
      Map(),
      "Mdm_id",
      11
    )
  }

  def global_id_from_Mdm = "global_id_from_Mdm"

  private def testHarnessCreator(enrichmentMapService: KeyedCoProcessFunction[String, KeyedUasp, KeyedCAData, Either[(UaspDto, String), UaspDto]]) = {
    val testHarness =
      new KeyedTwoInputStreamOperatorTestHarness[String, KeyedUasp, KeyedCAData, Either[(UaspDto, String), UaspDto]](
        new KeyedCoProcessOperator[String, KeyedUasp, KeyedCAData, Either[(UaspDto, String), UaspDto]](enrichmentMapService), keySelectorMain, keySelectorCa, Types.STRING
      )

    testHarness.open()


    testHarness

  }

}