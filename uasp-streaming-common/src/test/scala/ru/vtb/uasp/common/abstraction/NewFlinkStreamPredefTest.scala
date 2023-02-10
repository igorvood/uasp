package ru.vtb.uasp.common.abstraction

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.DataStream
import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import ru.vtb.uasp.common.abstraction.MiniPipeLineTrait.valuesTestDataDto
import ru.vtb.uasp.common.abstraction.NewFlinkStreamPredefTest.{dlqProcessFunctionError, flinkSinkProperties, flinkSinkPropertiesDlq, jsMaskedPath, listTestDataDto, outDtoWithErrorsFun, serviceDataDto, testDataDto}
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.mask.MaskedPredef.PathFactory
import ru.vtb.uasp.common.mask.MaskedStrPathWithFunName
import ru.vtb.uasp.common.mask.dto.JsMaskedPath
import ru.vtb.uasp.common.service.JsonConvertInService
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors, ServiceDataDto}
import ru.vtb.uasp.common.utils.config.kafka.KafkaPrdProperty

import java.util.Properties
import scala.collection.JavaConverters.mapAsJavaMapConverter
import scala.tools.jline_embedded.internal.Log.error

class NewFlinkStreamPredefTest extends AnyFlatSpec with MiniPipeLineTrait with Serializable {


    "NewFlinkStreamPredef.createProducerWithMetric " should " OK" in {

    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>
      NewFlinkStreamPredef.privateCreateProducerWithMetric(ds, serviceData = serviceDataDto, flinkSinkProperties, producerFactory)
    }

    pipeRun(listTestDataDto, flinkPipe)

    assertResult(1)(valuesTestDataDto.size)
    val dtoes = topicDataArray[TestDataDto](flinkSinkProperties)
    assertResult(1)(dtoes.size)
  }

  "NewFlinkStreamPredef.processAndDlqSinkWithMetric Ошибка, но маскирование не настроено" should " OK" in {

    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>
      val value = NewFlinkStreamPredef.processAndDlqSinkWithMetric[TestDataDto, TestDataDto](
        ds,
        serviceData = serviceDataDto,
        dlqProcessFunctionError,
        Some(flinkSinkPropertiesDlq),
        producerFactory[KafkaDto],
        new TestDataDtoMaskedSerializeService(jsMaskedPath = None))
    }

    pipeRun(listTestDataDto, flinkPipe)

    assertResult(1)(valuesTestDataDto.size)

    val dtoes1 = topicDataArray[TestDataDto](flinkSinkProperties)
    assertResult(0)(dtoes1.size)

    val dtoes = topicDataArray[KafkaDto](flinkSinkPropertiesDlq)
    assertResult(1)(dtoes.size)
    val either = JsonConvertInService.deserialize[OutDtoWithErrors[TestDataDto]](dtoes.head.value)(OutDtoWithErrors.outDtoWithErrorsJsonReads, serviceDataDto)
    val unit = outDtoWithErrorsFun(Some(testDataDto))
    assertResult(unit)( either.right.get)

  }

  "NewFlinkStreamPredef.processAndDlqSinkWithMetric Ошибка, маскирование настроено" should " OK" in {

    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>
      val value = NewFlinkStreamPredef.processAndDlqSinkWithMetric[TestDataDto, TestDataDto](
        ds,
        serviceData = serviceDataDto,
        dlqProcessFunctionError,
        Some(flinkSinkPropertiesDlq),
        producerFactory[KafkaDto],
        new TestDataDtoMaskedSerializeService(jsMaskedPath = jsMaskedPath))
    }

    pipeRun(listTestDataDto, flinkPipe)

    assertResult(1)(valuesTestDataDto.size)

    val dtoes1 = topicDataArray[TestDataDto](flinkSinkProperties)
    assertResult(0)(dtoes1.size)

    val dtoes = topicDataArray[KafkaDto](flinkSinkPropertiesDlq)
    assertResult(1)(dtoes.size)
    val outDto: OutDtoWithErrors[TestDataDto] = JsonConvertInService.deserialize[OutDtoWithErrors[TestDataDto]](dtoes.head.value)(OutDtoWithErrors.outDtoWithErrorsJsonReads, serviceDataDto).right.get
    val outDtoWith = outDtoWithErrorsFun(Some(testDataDto.copy(srt = "***MASKED***")))
    assertResult(outDtoWith)( outDto)

  }


}

object NewFlinkStreamPredefTest{

  private val testDataDto: TestDataDto = TestDataDto("st1", 12)
  private val listTestDataDto: List[TestDataDto] = List(testDataDto)

  protected val flinkSinkProperties = producerProps("topicName")
  protected val flinkSinkPropertiesDlq = producerProps("dlq_topicName")


  protected val  jsMaskedPath = Some(List(MaskedStrPathWithFunName("data.srt", "ru.vtb.uasp.common.mask.fun.StringMaskAll")).toJsonPath().right.get)

  protected implicit val serviceDataDto = ServiceDataDto("1", "2", "3")

  protected def outDtoWithErrorsFun[IN](in: Some[IN])= OutDtoWithErrors[IN](serviceDataDto, Some(this.getClass.getName),List("test error"), in)

  protected val dlqProcessFunctionError: DlqProcessFunction[TestDataDto, TestDataDto, OutDtoWithErrors[TestDataDto]] = new DlqProcessFunction[TestDataDto, TestDataDto, OutDtoWithErrors[TestDataDto]] {
    override def processWithDlq(dto: TestDataDto): Either[OutDtoWithErrors[TestDataDto], TestDataDto] = Left(outDtoWithErrorsFun(Some(dto)))
  }

  private def producerProps(topicName: String) = {
    val properties = new Properties()
    properties.putAll(Map("bootstrap.servers" -> "bootstrap.servers").asJava)

    val kafkaPrdProperty = KafkaPrdProperty(properties)
    FlinkSinkProperties(topicName, kafkaPrdProperty, None, None, None)
  }


}



