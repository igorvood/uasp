package ru.vtb.uasp.common.abstraction

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.DataStream
import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import ru.vtb.uasp.common.abstraction.FlinkStreamProducerPredef.StreamFactory
import ru.vtb.uasp.common.abstraction.MiniPipeLineTrait.valuesTestDataDto
import ru.vtb.uasp.common.abstraction.NewFlinkStreamPredefTest.{dlqProcessFunction, dlqProcessFunctionError, flinkSinkProperties, flinkSinkPropertiesDlq, jsMaskedPath, listTestDataDto, outDtoWithErrorsFun, serviceDataDto, testDataDto}
import ru.vtb.uasp.common.kafka.{FlinkSinkProperties, MaskProducerDTO}
import ru.vtb.uasp.common.mask.MaskedPredef.PathFactory
import ru.vtb.uasp.common.mask.MaskedStrPathWithFunName
import ru.vtb.uasp.common.service.JsonConvertInService
import ru.vtb.uasp.common.service.JsonConvertOutService.JsonPredef
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors, ServiceDataDto}
import ru.vtb.uasp.common.utils.config.kafka.KafkaPrdProperty

import java.util.Properties
import scala.collection.JavaConverters.mapAsJavaMapConverter

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
        Some(flinkSinkPropertiesDlq -> (s => s.serializeToBytes(None))),
        producerFactory[KafkaDto],
      )
    }

    processWithMaskedDqlErrNoMasked(flinkPipe)
  }

  "processWithMaskedDqlF Ошибка, но маскирование не настроено" should " OK" in {

    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>
      val value1 = ds.processWithMaskedDqlF(serviceDataDto, dlqProcessFunctionError, Some(flinkSinkPropertiesDlq -> (s => s.serializeToBytes(None))), producerFactory[KafkaDto])
    }

    processWithMaskedDqlErrNoMasked(flinkPipe)
  }

  "processWithMaskedDql Ошибка, но маскирование не настроено" should " OK" in {

    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>
      val service = new TestDataDtoMaskedSerializeService(jsMaskedPath = None)
      val value1 = ds.processWithMaskedDql(serviceDataDto, dlqProcessFunctionError, Some(flinkSinkPropertiesDlq -> service), producerFactory[KafkaDto])
    }

    processWithMaskedDqlErrNoMasked(flinkPipe)
  }

  private def processWithMaskedDqlErrNoMasked(flinkPipe: DataStream[TestDataDto] => Unit) = {
    pipeRun(listTestDataDto, flinkPipe)

    assertResult(1)(valuesTestDataDto.size)

    val dtoes1 = topicDataArray[TestDataDto](flinkSinkProperties)
    assertResult(0)(dtoes1.size)

    val dtoes = topicDataArray[KafkaDto](flinkSinkPropertiesDlq)
    assertResult(1)(dtoes.size)
    val either = JsonConvertInService.deserialize[OutDtoWithErrors[TestDataDto]](dtoes.head.value)(OutDtoWithErrors.outDtoWithErrorsJsonReads, serviceDataDto)
    val unit = outDtoWithErrorsFun(Some(testDataDto))
    assertResult(unit)(either.right.get)
  }

  "NewFlinkStreamPredef.processAndDlqSinkWithMetric нет ошибки, но маскирование не настроено" should " OK" in {

    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>


      val value = NewFlinkStreamPredef.processAndDlqSinkWithMetric[TestDataDto, TestDataDto](
        ds,
        serviceData = serviceDataDto,
        dlqProcessFunction,
        Some(flinkSinkPropertiesDlq -> (s => s.serializeToBytes(jsMaskedPath))),
        producerFactory[KafkaDto],

      )

      NewFlinkStreamPredef.privateCreateProducerWithMetric(value, serviceData = serviceDataDto, flinkSinkProperties, producerFactory)
    }

    processWithMaskedDqlNoErrNoMasked(flinkPipe)

  }

  "processWithMaskedDqlF нет ошибки, но маскирование не настроено" should " OK" in {

    val service = new TestDataDtoMaskedSerializeService(jsMaskedPath = None)

    val value1: MaskProducerDTO[OutDtoWithErrors[TestDataDto], KafkaDto] = flinkSinkPropertiesDlq.maskProducer[OutDtoWithErrors[TestDataDto]](s => s.serializeToBytes(jsMaskedPath))

    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>

      val value = ds.processWithMaskedDqlF(serviceDataDto, dlqProcessFunction, Some(flinkSinkPropertiesDlq -> (s => s.serializeToBytes(jsMaskedPath))), producerFactory[KafkaDto])

      NewFlinkStreamPredef.privateCreateProducerWithMetric(value, serviceData = serviceDataDto, flinkSinkProperties, producerFactory)
    }

    processWithMaskedDqlNoErrNoMasked(flinkPipe)

  }

  "processWithMaskedDql нет ошибки, но маскирование не настроено" should " OK" in {

    val service = new TestDataDtoMaskedSerializeService(jsMaskedPath = None)

    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>

      val value = ds.processWithMaskedDql(serviceDataDto, dlqProcessFunction, Some(flinkSinkPropertiesDlq -> service), producerFactory[KafkaDto])

      NewFlinkStreamPredef.privateCreateProducerWithMetric(value, serviceData = serviceDataDto, flinkSinkProperties, producerFactory)
    }

    processWithMaskedDqlNoErrNoMasked(flinkPipe)

  }


  private def processWithMaskedDqlNoErrNoMasked(flinkPipe: DataStream[TestDataDto] => Unit) = {
    pipeRun(listTestDataDto, flinkPipe)

    assertResult(1)(valuesTestDataDto.size)

    val dtoes1 = topicDataArray[TestDataDto](flinkSinkProperties)
    assertResult(1)(dtoes1.size)

    assertResult(testDataDto)(dtoes1.head)

    val dtoes = topicDataArray[KafkaDto](flinkSinkPropertiesDlq)
    assertResult(0)(dtoes.size)
  }

  "NewFlinkStreamPredef.processAndDlqSinkWithMetric Ошибка, маскирование настроено" should " OK" in {

    val service = new TestDataDtoMaskedSerializeService(jsMaskedPath = jsMaskedPath)
    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>

      val value = NewFlinkStreamPredef.processAndDlqSinkWithMetric[TestDataDto, TestDataDto](
        ds,
        serviceData = serviceDataDto,
        dlqProcessFunctionError,
        Some(flinkSinkPropertiesDlq.copy(jsMaskedPath = jsMaskedPath) -> (s => s.serializeToBytes(jsMaskedPath))),
        producerFactory[KafkaDto],
      )
    }

    processWithMaskedDqlErrMasked(flinkPipe)

  }

  "processWithMaskedDqlF Ошибка, маскирование настроено" should " OK" in {

    val service = new TestDataDtoMaskedSerializeService(jsMaskedPath = jsMaskedPath)
    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>
      val value1 = ds.processWithMaskedDqlF(serviceDataDto, dlqProcessFunctionError, Some(flinkSinkPropertiesDlq.copy(jsMaskedPath = jsMaskedPath) -> (s => s.serializeToBytes(jsMaskedPath))), producerFactory[KafkaDto])
    }

    processWithMaskedDqlErrMasked(flinkPipe)

  }

  "processWithMaskedDql Ошибка, маскирование настроено" should " OK" in {

    val service = new TestDataDtoMaskedSerializeService(jsMaskedPath = jsMaskedPath)
    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>
      val value1 = ds.processWithMaskedDql(serviceDataDto, dlqProcessFunctionError, Some(flinkSinkPropertiesDlq.copy(jsMaskedPath = jsMaskedPath) -> service), producerFactory[KafkaDto])
    }

    processWithMaskedDqlErrMasked(flinkPipe)

  }


  private def processWithMaskedDqlErrMasked(flinkPipe: DataStream[TestDataDto] => Unit) = {
    pipeRun(listTestDataDto, flinkPipe)

    assertResult(1)(valuesTestDataDto.size)

    val dtoes1 = topicDataArray[TestDataDto](flinkSinkProperties)
    assertResult(0)(dtoes1.size)

    val dtoes = topicDataArray[KafkaDto](flinkSinkPropertiesDlq)
    assertResult(1)(dtoes.size)
    val outDto: OutDtoWithErrors[TestDataDto] = JsonConvertInService.deserialize[OutDtoWithErrors[TestDataDto]](dtoes.head.value)(OutDtoWithErrors.outDtoWithErrorsJsonReads, serviceDataDto).right.get
    val outDtoWith = outDtoWithErrorsFun(Some(testDataDto.copy(srt = "***MASKED***")))
    assertResult(outDtoWith)(outDto)
  }
}

object NewFlinkStreamPredefTest {

  private val testDataDto: TestDataDto = TestDataDto("st1", 12)
  private val listTestDataDto: List[TestDataDto] = List(testDataDto)

  protected val flinkSinkProperties = producerProps("topicName")
  protected val flinkSinkPropertiesDlq = producerProps("dlq_topicName")


  protected val jsMaskedPath = Some(List(MaskedStrPathWithFunName("data.srt", "ru.vtb.uasp.common.mask.fun.StringMaskAll")).toJsonPath().right.get)

  protected implicit val serviceDataDto = ServiceDataDto("1", "2", "3")

  protected def outDtoWithErrorsFun[IN](in: Some[IN]) = OutDtoWithErrors[IN](serviceDataDto, Some(this.getClass.getName), List("test error"), in)

  protected val dlqProcessFunctionError: DlqProcessFunction[TestDataDto, TestDataDto, OutDtoWithErrors[TestDataDto]] = new DlqProcessFunction[TestDataDto, TestDataDto, OutDtoWithErrors[TestDataDto]] {
    override def processWithDlq(dto: TestDataDto): Either[OutDtoWithErrors[TestDataDto], TestDataDto] = Left(outDtoWithErrorsFun(Some(dto)))
  }

  protected val dlqProcessFunction: DlqProcessFunction[TestDataDto, TestDataDto, OutDtoWithErrors[TestDataDto]] = new DlqProcessFunction[TestDataDto, TestDataDto, OutDtoWithErrors[TestDataDto]] {
    override def processWithDlq(dto: TestDataDto): Either[OutDtoWithErrors[TestDataDto], TestDataDto] = Right(dto)
  }


  private def producerProps(topicName: String) = {
    val properties = new Properties()
    properties.putAll(Map("bootstrap.servers" -> "bootstrap.servers").asJava)

    val kafkaPrdProperty = KafkaPrdProperty(properties)
    FlinkSinkProperties(topicName, kafkaPrdProperty, None, None, None)
  }


}



