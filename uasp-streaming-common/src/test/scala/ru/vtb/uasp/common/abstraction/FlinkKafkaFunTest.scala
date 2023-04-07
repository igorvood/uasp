package ru.vtb.uasp.common.abstraction

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.DataStream
import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import play.api.libs.json.{JsValue, Json}
import ru.vtb.uasp.common.abstraction.FlinkKafkaFunTest._
import ru.vtb.uasp.common.abstraction.FlinkStreamProducerPredef.StreamFactory
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.mask.MaskedPredef.PathFactory
import ru.vtb.uasp.common.mask.MaskedStrPathWithFunName
import ru.vtb.uasp.common.service.JsonConvertInService
import ru.vtb.uasp.common.service.JsonConvertOutService.{JsonPredef, serializeToBytes}
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors, PropertyWithSerializer, ServiceDataDto}
import ru.vtb.uasp.common.test.MiniPipeLineTrait
import ru.vtb.uasp.common.test.MiniPipeLineTrait.valuesTestDataDto
import ru.vtb.uasp.common.utils.config.kafka.KafkaPrdProperty

import java.util.Properties

class FlinkKafkaFunTest extends AnyFlatSpec with MiniPipeLineTrait with Serializable {


  "NewFlinkStreamPredef.createProducerWithMetric " should " OK" in {

    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>
      FlinkKafkaFun.privateCreateProducerWithMetric(ds, serviceData = serviceDataDto, flinkSinkProperties, producerFactory)
    }

    pipeRun(listTestDataDto, flinkPipe)

    assertResult(1)(valuesTestDataDto.size)
    val dtoes = topicDataArray[TestDataDto](flinkSinkProperties)
    assertResult(1)(dtoes.size)
  }

  "NewFlinkStreamPredef.processAndDlqSinkWithMetric Ошибка, но маскирование не настроено" should " OK" in {

    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>
      val value = FlinkKafkaFun.processAndDlqSinkWithMetric[TestDataDto, TestDataDto](
        ds,
        serviceData = serviceDataDto,
        dlqProcessFunctionError,
        Some(PropertyWithSerializer(flinkSinkPropertiesDlq, {
          _.serializeToKafkaJsValue
        })),
        producerFactory[KafkaDto],
      )
    }

    processWithMaskedDqlErrNoMasked(flinkPipe)
  }

  "processWithMaskedDqlF Ошибка, но маскирование не настроено" should " OK" in {

    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>
      val value1 = ds.processWithMaskedDqlF(
        serviceDataDto,
        dlqProcessFunctionError,
        Some(PropertyWithSerializer(flinkSinkPropertiesDlq, {
          _.serializeToKafkaJsValue
        })),
        producerFactory[KafkaDto]
      )
    }

    processWithMaskedDqlErrNoMasked(flinkPipe)
  }


  "processWithMaskedDqlF jsValue Ошибка, но маскирование не настроено" should " OK" in {

    val values = listTestDataDto
      .map(d => Json.toJson(d))

    val flinkPipe: DataStream[JsValue] => Unit = { ds =>

      val value1 = ds.processWithMaskedDqlF(
        serviceDataDto,
        dlqProcessFunctionErrorJs,
        Some(PropertyWithSerializer(flinkSinkPropertiesDlq, {
          _.serializeToKafkaJsValue
        })),
        producerFactory[KafkaDto]
      )
    }


    pipeRun(values, flinkPipe)

    assertResult(1)(valuesTestDataDto.size)

    val dtoes1 = topicDataArray[TestDataDto](flinkSinkProperties)
    assertResult(0)(dtoes1.size)

    val dtoes = topicDataArray[KafkaDto](flinkSinkPropertiesDlq)
    assertResult(1)(dtoes.size)
    val either = JsonConvertInService.deserialize[OutDtoWithErrors[TestDataDto]](dtoes.head.value)(OutDtoWithErrors.outDtoWithErrorsJsonReads, serviceDataDto)
    val unit = outDtoWithErrorsFun(Some(testDataDto))
    assertResult(unit)(either.right.get)

  }

  "processWithMaskedDql1 Ошибка, но маскирование не настроено" should " OK" in {
    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>
      val value1 = ds.processWithMaskedDqlFC(
        serviceDataDto,
        dlqProcessFunctionError,
        Some(flinkSinkPropertiesDlq -> { (q, w) => serializeToBytes[OutDtoWithErrors[TestDataDto]](q, w) }),
        producerFactory[KafkaDto]
      )
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


      val value = FlinkKafkaFun.processAndDlqSinkWithMetric[TestDataDto, TestDataDto](
        ds,
        serviceData = serviceDataDto,
        dlqProcessFunction,
        Some(PropertyWithSerializer[OutDtoWithErrors[TestDataDto]](flinkSinkPropertiesDlq, {
          _.serializeToKafkaJsValue
        })),
        producerFactory[KafkaDto],

      )

      FlinkKafkaFun.privateCreateProducerWithMetric(value, serviceData = serviceDataDto, flinkSinkProperties, producerFactory)
    }

    processWithMaskedDqlNoErrNoMasked(flinkPipe)

  }

  "processWithMaskedDqlF нет ошибки, но маскирование не настроено" should " OK" in {

    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>

      val value = ds.processWithMaskedDqlF(
        serviceDataDto,
        dlqProcessFunction,
        Some(PropertyWithSerializer[OutDtoWithErrors[TestDataDto]](flinkSinkPropertiesDlq, {
          _.serializeToKafkaJsValue
        })),
        producerFactory[KafkaDto])

      FlinkKafkaFun.privateCreateProducerWithMetric(value, serviceData = serviceDataDto, flinkSinkProperties, producerFactory)
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

    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>

      val value = FlinkKafkaFun.processAndDlqSinkWithMetric[TestDataDto, TestDataDto](
        ds,
        serviceData = serviceDataDto,
        dlqProcessFunctionError,
        Some(PropertyWithSerializer[OutDtoWithErrors[TestDataDto]](flinkSinkPropertiesDlq.copy(jsMaskedPath = jsMaskedPathDLQOk), {
          _.serializeToKafkaJsValue
        })),
        producerFactory[KafkaDto],
      )
    }

    processWithMaskedDqlErrMasked(flinkPipe)

  }

  "processWithMaskedDqlF Ошибка, маскирование настроено" should " OK" in {

    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>
      val value1 = ds.processWithMaskedDqlF(
        serviceDataDto,
        dlqProcessFunctionError,
        Some(PropertyWithSerializer[OutDtoWithErrors[TestDataDto]](flinkSinkPropertiesDlq.copy(jsMaskedPath = jsMaskedPathDLQOk), {
          _.serializeToKafkaJsValue
        })),
        producerFactory[KafkaDto]
      )
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
    assert(outDtoWith == outDto)
  }

  "maskedProducerF Ошибка при маскировании основного сообщения, маскирование DLQ без ошибки " should " OK" in {

    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>

      val value = ds.maskedProducerF(
        serviceDataDto,
        PropertyWithSerializer(flinkSinkProperties.copy(jsMaskedPath = jsMaskedPathErrMainERR), {
          _.serializeToKafkaJsValue
        }),
        Some(PropertyWithSerializer[OutDtoWithErrors[TestDataDto]](flinkSinkPropertiesDlq.copy(jsMaskedPath = jsMaskedPathDLQOk), {
          _.serializeToKafkaJsValue
        })),
        producerFactory[KafkaDto]
      )
    }


    maskedProducerFErrMainDLQOk(flinkPipe)
  }

  "maskedProducerFErr маскирование основного сообщения с ошибкой, маскирование DLQ без ошибки " should " OK" in {

    implicit val serviceDataDtoIm: ServiceDataDto = serviceDataDto
    val flinkPipe: DataStream[OutDtoWithErrors[TestDataDto]] => Unit = { ds =>


      val propertyWithSerializer = PropertyWithSerializer[OutDtoWithErrors[TestDataDto]](
        flinkSinkProperties.copy(jsMaskedPath = jsMaskedPathDLQErr),
        {
          _.serializeToKafkaJsValue
        }
      )
      val value = ds.maskedProducerFErr(
        serviceDataDto,
        propertyWithSerializer,
        producerFactory,
        {
          _.serializeToKafkaJsValue
        }
      )
    }
    val someString = Some("TEST pos")
    val dto: List[OutDtoWithErrors[TestDataDto]] = listTestDataDto.map(td => {

      OutDtoWithErrors(serviceDataDto, someString, List("some error"), Some(td))
    })
    pipeRun(dto, flinkPipe)

    assertResult(1)(valuesTestDataDto.size)

    val listErrs = topicDataArray[KafkaDto](flinkSinkProperties)
    val list = listErrs
      .map { by =>
        JsonConvertInService.deserialize[OutDtoWithErrors[TestDataDto]](by.value).right.get
      }
    assertResult(1)(list.size)

    val errs = list.head
    assert(errs.data.isEmpty)
    assert(errs.errorPosition.contains("ru.vtb.uasp.common.abstraction.FlinkKafkaFun$$anon$1"))
    assert(errs.errors == List("createProducerWithMetric: Unable to mask dto ru.vtb.uasp.common.service.dto.OutDtoWithErrors. Masked rule Some(JsMaskedPathObject(Map(data -> JsMaskedPathObject(Map(srt -> JsNumberMaskedPathValue(NumberMaskAll()))))))",
      "Unable to masked value wrapper class  class play.api.libs.json.JsString with function -> class ru.vtb.uasp.common.mask.dto.JsNumberMaskedPathValue"
    ))
  }

  "maskedProducer Ошибка при маскировании основного сообщения, маскирование DLQ без ошибки " should " OK" in {
    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>
      val value = ds.maskedProducerF(
        serviceDataDto,
        PropertyWithSerializer(flinkSinkProperties.copy(jsMaskedPath = jsMaskedPathErrMainERR), {
          _.serializeToKafkaJsValue
        }),
        Some(PropertyWithSerializer(flinkSinkPropertiesDlq.copy(jsMaskedPath = jsMaskedPathDLQOk), {
          _.serializeToKafkaJsValue
        })),
        producerFactory[KafkaDto]
      )
    }

    maskedProducerFErrMainDLQOk(flinkPipe)
  }

  private def maskedProducerFErrMainDLQOk(flinkPipe: DataStream[TestDataDto] => Unit) = {
    pipeRun(listTestDataDto, flinkPipe)

    assertResult(1)(valuesTestDataDto.size)

    val kafkaDtos = topicDataArray[KafkaDto](flinkSinkPropertiesDlq)
    assertResult(1)(kafkaDtos.size)
    val outDto: OutDtoWithErrors[TestDataDto] = JsonConvertInService.deserialize[OutDtoWithErrors[TestDataDto]](kafkaDtos.head.value)(OutDtoWithErrors.outDtoWithErrorsJsonReads, serviceDataDto).right.get

    val outDtoWith = outDtoWithErrorsFun(Some(testDataDto.copy(srt = "***MASKED***")))
      .copy(
        errorPosition = outDto.errorPosition,
        errors = List(
          "createProducerWithMetric: Unable to mask dto ru.vtb.uasp.common.abstraction.TestDataDto. Masked rule Some(JsMaskedPathObject(Map(srt -> JsNumberMaskedPathValue(NumberMaskAll()))))",
          "Unable to masked value wrapper class  class play.api.libs.json.JsString with function -> class ru.vtb.uasp.common.mask.dto.JsNumberMaskedPathValue"
        )
      )
    assertResult(outDtoWith)(outDto)
  }

  "maskedProducerF Ошибка при маскировании основного сообщения, маскирование DLQ с ошибкой " should " OK" in {

    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>
      val value = ds.maskedProducerF(
        serviceDataDto,
        PropertyWithSerializer(flinkSinkProperties.copy(jsMaskedPath = jsMaskedPathErrMainERR), {
          _.serializeToKafkaJsValue
        }),
        Some(PropertyWithSerializer(flinkSinkPropertiesDlq.copy(jsMaskedPath = jsMaskedPathDLQErr), {
          _.serializeToKafkaJsValue
        })),
        producerFactory[KafkaDto]
      )
    }
    pipeRun(listTestDataDto, flinkPipe)

    assertResult(1)(valuesTestDataDto.size)

    val kafkaDtos = topicDataArray[KafkaDto](flinkSinkPropertiesDlq)
    assertResult(1)(kafkaDtos.size)
    val outDto: OutDtoWithErrors[TestDataDto] = JsonConvertInService.deserialize[OutDtoWithErrors[TestDataDto]](kafkaDtos.head.value)(OutDtoWithErrors.outDtoWithErrorsJsonReads, serviceDataDto).right.get

    val outDtoWith = outDtoWithErrorsFun(Some(testDataDto.copy(srt = "***MASKED***")))
      .copy(
        errorPosition = outDto.errorPosition,
        errors = List(
          "processAndDlqSinkWithMetric: Unable to mask dto scala.collection.immutable.$colon$colon. Masked rule Some(JsMaskedPathObject(Map(data -> JsMaskedPathObject(Map(srt -> JsNumberMaskedPathValue(NumberMaskAll()))))))",
          "Unable to masked value wrapper class  class play.api.libs.json.JsString with function -> class ru.vtb.uasp.common.mask.dto.JsNumberMaskedPathValue"
        ),
        data = None
      )
    assertResult(outDtoWith)(outDto)
  }

  "maskedProducerF успех при маскировании основного сообщения, маскирование DLQ с ошибкой " should " OK" in {

    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>
      val value = ds.maskedProducerF(
        serviceDataDto,
        PropertyWithSerializer(flinkSinkProperties.copy(jsMaskedPath = jsMaskedPathErrMainOk), {
          _.serializeToKafkaJsValue
        }),
        Some(PropertyWithSerializer(flinkSinkPropertiesDlq.copy(jsMaskedPath = jsMaskedPathDLQErr), {
          _.serializeToKafkaJsValue
        })),
        producerFactory[KafkaDto]
      )
    }
    pipeRun(listTestDataDto, flinkPipe)

    assertResult(1)(valuesTestDataDto.size)

    val kafkaDtos = topicDataArray[KafkaDto](flinkSinkProperties)
    assertResult(1)(kafkaDtos.size)
    val outDto: TestDataDto = JsonConvertInService.deserialize[TestDataDto](kafkaDtos.head.value)(TestDataDto.uaspJsonReads, serviceDataDto).right.get

    assertResult(listTestDataDto.head.copy(srt = "***MASKED***"))(outDto)
  }

}

object FlinkKafkaFunTest {

  private val testDataDto: TestDataDto = TestDataDto("st1", 12)
  private val listTestDataDto: List[TestDataDto] = List(testDataDto)

  protected val flinkSinkProperties = producerProps("topicName")
  protected val flinkSinkPropertiesDlq = producerProps("dlq_topicName")


  protected val jsMaskedPathDLQOk = Some(List(MaskedStrPathWithFunName("data.srt", "ru.vtb.uasp.common.mask.fun.StringMaskAll")).toJsonPath.right.get)
  protected val jsMaskedPathDLQErr = Some(List(MaskedStrPathWithFunName("data.srt", "ru.vtb.uasp.common.mask.fun.NumberMaskAll")).toJsonPath.right.get)
  protected val jsMaskedPathErrMainERR = Some(List(MaskedStrPathWithFunName("srt", "ru.vtb.uasp.common.mask.fun.NumberMaskAll")).toJsonPath.right.get)
  protected val jsMaskedPathErrMainOk = Some(List(MaskedStrPathWithFunName("srt", "ru.vtb.uasp.common.mask.fun.StringMaskAll")).toJsonPath.right.get)

  protected implicit val serviceDataDto = ServiceDataDto("1", "2", "3")

  protected def outDtoWithErrorsFun[IN](in: Some[IN]) = OutDtoWithErrors[IN](serviceDataDto, Some(this.getClass.getName), List("test error"), in)

  protected val dlqProcessFunctionError: DlqProcessFunction[TestDataDto, TestDataDto, OutDtoWithErrors[TestDataDto]] = new DlqProcessFunction[TestDataDto, TestDataDto, OutDtoWithErrors[TestDataDto]] {
    override def processWithDlq(dto: TestDataDto): Either[OutDtoWithErrors[TestDataDto], TestDataDto] = Left(outDtoWithErrorsFun(Some(dto)))
  }

  protected val dlqProcessFunctionErrorJs: DlqProcessFunction[JsValue, JsValue, OutDtoWithErrors[JsValue]] = new DlqProcessFunction[JsValue, JsValue, OutDtoWithErrors[JsValue]] {
    override def processWithDlq(dto: JsValue): Either[OutDtoWithErrors[JsValue], JsValue] = Left(outDtoWithErrorsFun(Some(dto)))
  }

  protected val dlqProcessFunction: DlqProcessFunction[TestDataDto, TestDataDto, OutDtoWithErrors[TestDataDto]] = new DlqProcessFunction[TestDataDto, TestDataDto, OutDtoWithErrors[TestDataDto]] {
    override def processWithDlq(dto: TestDataDto): Either[OutDtoWithErrors[TestDataDto], TestDataDto] = Right(dto)
  }


  private def producerProps(topicName: String) = {
    val properties = new Properties()

    properties.put("bootstrap.servers", "bootstrap.servers")

    val kafkaPrdProperty = KafkaPrdProperty(properties)
    FlinkSinkProperties(topicName, kafkaPrdProperty, None, None, None)
  }


}



