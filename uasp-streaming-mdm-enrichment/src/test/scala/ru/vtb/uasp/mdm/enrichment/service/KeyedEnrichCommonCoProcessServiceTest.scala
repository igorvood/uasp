package ru.vtb.uasp.mdm.enrichment.service

import org.apache.flink.api.scala.typeutils.Types
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction
import org.apache.flink.streaming.api.operators.co.KeyedCoProcessOperator
import org.apache.flink.streaming.util.KeyedTwoInputStreamOperatorTestHarness
import org.scalatest.flatspec.AnyFlatSpec
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.kafka.{FlinkConsumerProperties, FlinkSinkProperties}
import ru.vtb.uasp.common.service.dto.{OutDtoWithErrors, ServiceDataDto}
import ru.vtb.uasp.common.utils.config.kafka.{KafkaCnsProperty, KafkaPrdProperty}
import ru.vtb.uasp.mdm.enrichment.EnrichmentJob.{keySelectorCa, keySelectorMain}
import ru.vtb.uasp.mdm.enrichment.TestConst._
import ru.vtb.uasp.mdm.enrichment.service.KeyedEnrichCommonCoProcessServiceTest._
import ru.vtb.uasp.mdm.enrichment.service.dto.{KeyedCAData, KeyedUasp}
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich._
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf.InputFormatEnum._

import java.util.Properties
import scala.annotation.tailrec
import scala.collection.JavaConverters.collectionAsScalaIterableConverter

class KeyedEnrichCommonCoProcessServiceTest extends AnyFlatSpec {

  implicit val serviceDataDto = ServiceDataDto("йц", "ук", "ке")


  behavior of "KeyEnrichmentMapServiceTest"

  it should " test enrichMainStream with Optional value but it is empty" in {
    val allProperty = enrichPropMap(true)
    val enrichmentMapService = new KeyedEnrichCommonCoProcessService(serviceDataDto,allProperty.commonEnrichProperty.get)
    val testHarness = createTestHarness(enrichmentMapService)

    testHarness.open()

    val uaspDto = dataForState(false)

    val keyedUasp = KeyedUasp(uaspDto.id, uaspDto)

    testHarness.processElement1(keyedUasp, 0)

    assertResult(1)(testHarness.extractOutputStreamRecords().size())

    val actualRichedValue = testHarness.extractOutputStreamRecords().get(0).getValue

    assert(actualRichedValue.isRight)
    assertResult(uaspDto)(actualRichedValue.right.get)


  }

  it should " test enrichMainStream with non Optional value but it is empty" in {
    val allProperty = enrichPropMap(false)
    val enrichmentMapService = new KeyedEnrichCommonCoProcessService( serviceDataDto, allProperty.commonEnrichProperty.get)

    val testHarness = createTestHarness(enrichmentMapService)

    testHarness.open()

    val uaspDto = dataForState(false)
    val keyedUasp = KeyedUasp(uaspDto.id, uaspDto)

    testHarness.processElement1(keyedUasp, 0)

    assertResult(1)(testHarness.extractOutputStreamRecords().size())

    val actualRichedValue = testHarness.extractOutputStreamRecords().get(0).getValue

    assert(actualRichedValue.isLeft)

    assertResult(List("Value from field String to String_toFieldName is null, but value isn't optional"))(actualRichedValue.left.get.errors)

    assertResult(uaspDto)(actualRichedValue.left.get.data.get)

  }

  it should " test enrichMainStream with non Optional value but it is non empty" in {
    val allProperty = enrichPropMap(false)

    val commonEnrichProperty = allProperty.commonEnrichProperty.get
    val enrichmentMapService = new KeyedEnrichCommonCoProcessService(serviceDataDto, commonEnrichProperty)

    val testHarness = createTestHarness(enrichmentMapService)

    testHarness.open()

    val commonDataUaspDto = dataForState(false)
    val commonData = commonEnrichProperty.validateFieldsAndExtractData(commonDataUaspDto).right.get

    val mainDataUaspDto = dataForState(true)


    val mainData = KeyedUasp(mainDataUaspDto.id, mainDataUaspDto)
    testHarness.processElement2(commonData, 0)
    testHarness.processElement1(mainData, 0)

    val outStream = testHarness.extractOutputStreamRecords().asScala.toList.map(q => q.getValue)
    assertResult(1)(outStream.size)

    val mainDto = outStream.head

    assert(mainDto.isRight)

    val commonUasp = commonData.data
    val mainUasp = mainDto.right.get

    assertResult(Map("String_toFieldName" -> "String"))(mainUasp.dataString)
    assertResult(Map("Int_toFieldName" -> 3))(mainUasp.dataInt)
    assertResult(Map("Long_toFieldName" -> 2))(mainUasp.dataLong)
    assertResult(Map("Float_toFieldName" -> 4.0))(mainUasp.dataFloat)
    assertResult(Map("Double_toFieldName" -> 5.0))(mainUasp.dataDouble)
    assertResult(Map("Bigdecimal_toFieldName" -> BigDecimal(1)))(mainUasp.dataDecimal)
    assertResult(Map("Boolean_toFieldName" -> true))(mainUasp.dataBoolean)
  }

  it should " test data for stream with Optional value but it is empty" in {
    val allProperty = enrichPropMap(true)

    val commonEnrichProperty =  allProperty.commonEnrichProperty.get
    val enrichmentMapService = new KeyedEnrichCommonCoProcessService(serviceDataDto, commonEnrichProperty)
    val testHarness = createTestHarness(enrichmentMapService)

    testHarness.open()

    val caDto1 = dataForState(false)
    val caDto = commonEnrichProperty.validateFieldsAndExtractData(caDto1).right.get

    testHarness.processElement2(caDto, 0)

    val list = testHarness.extractOutputStreamRecords()

    assertResult(0)(list.size())

    val value1 = enrichmentMapService.getRuntimeContext.getState(enrichmentMapService.valueStateDescriptor).value()


    assertResult(Map("Boolean" -> "true", "Double" -> "5.0", "Bigdecimal" -> "1", "Long" -> "2", "Float" -> "4.0", "Int" -> "3", "String" -> "String"))(value1)

  }

  it should " test data for stream with non Optional value but it is non empty" in {
    val allProperty = enrichPropMap(false)
    val commonEnrichProperty = allProperty.commonEnrichProperty.get


    val enrichmentMapService = new KeyedEnrichCommonCoProcessService(serviceDataDto,commonEnrichProperty)

    val testHarness = createTestHarness(enrichmentMapService)

    testHarness.open()

    val caDto = dataForState(false)
    val keyedCAData = commonEnrichProperty.validateFieldsAndExtractData(caDto).right.get


    testHarness.processElement2(keyedCAData, 0)

    val stateWithOneRec = enrichmentMapService.getRuntimeContext.getState(enrichmentMapService.valueStateDescriptor).value()
    val key = "String"

    assertResult(7)(stateWithOneRec.size)

    assertResult(Map("Boolean" -> "true", "Double" -> "5.0", "Bigdecimal" -> "1", "Long" -> "2", "Float" -> "4.0", "Int" -> "3", "String" -> "String"))(stateWithOneRec)

    testHarness.processElement1(KeyedUasp(inputDto.id, inputDto), 0)

    val list = testHarness.extractOutputStreamRecords()
    assertResult(1)(list.size())

    list.stream().forEach { m =>
      val either = m.getValue
      assert(either.isRight)


      val dto = either.right.get
      assertResult(1)(dto.dataString.size)
      assertResult(1)(dto.dataDecimal.size)
      assertResult(1)(dto.dataLong.size)
      assertResult(1)(dto.dataInt.size)
      assertResult(1)(dto.dataFloat.size)
      assertResult(1)(dto.dataDouble.size)
      assertResult(1)(dto.dataBoolean.size)
    }


    val commonDataUaspDto = dataForState(false).copy(id = "2", dataString = Map(key -> "String1"))
    val commonData = commonEnrichProperty.validateFieldsAndExtractData(commonDataUaspDto).right.get

    testHarness.processElement2(commonData, 0)

    val stateWithTwoRec = enrichmentMapService.getRuntimeContext.getState(enrichmentMapService.valueStateDescriptor).value()

    assertResult(Map("Boolean" -> "true", "Double" -> "5.0", "Bigdecimal" -> "1", "Long" -> "2", "Float" -> "4.0", "Int" -> "3", "String" -> "String1"))(stateWithTwoRec)

  }

  def createTestHarness(globalIdEnrichmentMapService: KeyedCoProcessFunction[String, KeyedUasp, KeyedCAData, Either[OutDtoWithErrors[UaspDto], UaspDto]]) = {
    val keyedCoProcessOperator = new KeyedCoProcessOperator(globalIdEnrichmentMapService)

    val harness = new KeyedTwoInputStreamOperatorTestHarness[String, KeyedUasp, KeyedCAData, Either[OutDtoWithErrors[UaspDto], UaspDto]](keyedCoProcessOperator, keySelectorMain, keySelectorCa, Types.STRING)

    harness
  }
}

object KeyedEnrichCommonCoProcessServiceTest {
  private val fields: EnrichFields = EnrichFields(
    fromFieldName = s" field",
    fromFieldType = "String",
    toFieldName = s" toFieldName",
    isOptionalEnrichValue = true
  )

  def enrichPropMap(isOptional: Boolean) = new AllEnrichProperty(Some(em(isOptional)), Some(newGlobalIdEnrichProperty), mainEnrichProperty)

  private def mainEnrichProperty: MainEnrichProperty = new MainEnrichProperty(
    fromTopic = FlinkConsumerProperties(calcTopicName("From" + mainStreamName), KafkaCnsProperty(properties)),
    toTopicProp = FlinkSinkProperties(calcTopicName("To" + mainStreamName), KafkaPrdProperty(properties), None, None, None) ,
    dlqTopicProp = None
  )


  private def newGlobalIdEnrichProperty = new GlobalIdEnrichProperty(
    fromTopic = FlinkConsumerProperties(calcTopicName("FromGlobal"), KafkaCnsProperty(properties)),
    dlqTopicProp = None,
    globalEnrichFields = fields,
    keySelectorMain = KeySelectorProp(true),
    keySelectorEnrich = KeySelectorProp(true),
    fields = List(),
    inputDataFormat = UaspDtoFormat
  )

  private def em(isOptional: Boolean): CommonEnrichProperty = {
    val fieldses = uaspType.map(t =>
      EnrichFields(
        fromFieldName = s"$t",
        fromFieldType = t,
        toFieldName = s"${t}_toFieldName",
        isOptionalEnrichValue = isOptional
      ))

    val name = CommonEnrichProperty.getClass.getSimpleName
    new CommonEnrichProperty(
      fromTopic = FlinkConsumerProperties(calcTopicName("From" + name), KafkaCnsProperty(properties)),
      dlqTopicProp = None,
      keySelectorMain = KeySelectorProp(true),
      keySelectorEnrich = KeySelectorProp(true),
      fields = fieldses,
      inputDataFormat = UaspDtoFormat
    )


  }

  protected def dataForState(isOptional: Boolean): UaspDto = {

    @tailrec
    def update(uasp: UaspDto, listType: List[String]): UaspDto = {
      listType match {
        case x :: xs =>
          val dto = if (!isOptional) {
            x.toUpperCase match {
              case "STRING" => uasp.copy(dataString = uasp.dataString + (x -> x))
              case "BIGDECIMAL" => uasp.copy(dataDecimal = uasp.dataDecimal + (x -> 1))
              case "LONG" => uasp.copy(dataLong = uasp.dataLong + (x -> 2))
              case "INT" => uasp.copy(dataInt = uasp.dataInt + (x -> 3))
              case "FLOAT" => uasp.copy(dataFloat = uasp.dataFloat + (x -> 4))
              case "DOUBLE" => uasp.copy(dataDouble = uasp.dataDouble + (x -> 5))
              case "BOOLEAN" => uasp.copy(dataBoolean = uasp.dataBoolean + (x -> true))
              case _ => throw new IllegalStateException(s"Unsupported type $x")

            }
          }
          else uasp
          update(dto, xs)
        case Nil => uasp
      }


    }

    update(inputDto, uaspType)

  }

  def inputDto =
    UaspDto("uasp id", Map(), Map(), Map(), Map(), Map(), Map(), Map(), "uuid", 1L)

  def prevErrorValue = "error found before this"

  private def uaspType = List("String", "Bigdecimal", "Long", "Int", "Float", "Double", "Boolean")


}