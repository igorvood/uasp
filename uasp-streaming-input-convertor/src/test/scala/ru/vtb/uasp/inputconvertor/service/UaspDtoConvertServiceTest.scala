package ru.vtb.uasp.inputconvertor.service

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.DataStream
import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json._
import ru.vtb.uasp.common.abstraction.FlinkStreamProducerPredef.StreamFactory
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.mask.dto.{JsMaskedPath, JsMaskedPathError}
import ru.vtb.uasp.common.service.JsonConvertOutService.{JsonPredef, serializeToBytes}
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors, ServiceDataDto}
import ru.vtb.uasp.common.test.MiniPipeLineTrait
import ru.vtb.uasp.common.utils.config.kafka.KafkaPrdProperty
import ru.vtb.uasp.inputconvertor.entity.InputMessageType
import ru.vtb.uasp.inputconvertor.factory.UaspDtoParser
import ru.vtb.uasp.inputconvertor.service.UaspDtoConvertServiceTest.{flinkSinkPropertiesDlq, flinkSinkPropertiesOK, serviceDataDto, uaspDto}
import ru.vtb.uasp.inputconvertor.service.dto.UaspAndKafkaKey
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel
import ru.vtb.uasp.validate.DroolsValidator
import ru.vtb.uasp.validate.entity.ValidateMsg

import java.util.Properties
import scala.collection.JavaConverters.mapAsJavaMapConverter

class UaspDtoConvertServiceTest extends AnyFlatSpec with MiniPipeLineTrait with Serializable {

  private val convertService: (UaspDtoParser) => UaspDtoConvertService = { p =>
    new UaspDtoConvertService(p, new MockDroolsValidator(), ServiceDataDto("1", "2", "3"), Map.empty)
  }


  def rp(data: List[InputMessageType], MockUaspDtoParserList: List[JsResult[UaspDto]]): TestResult = {
    val convertServiceV = convertService(MockUaspDtoParser(MockUaspDtoParserList))

    val flinkPipe: DataStream[InputMessageType] => Unit = { ds =>

      val sinkDlqProperty: Option[(FlinkSinkProperties, (OutDtoWithErrors[JsValue], Option[JsMaskedPath]) => Either[List[JsMaskedPathError], KafkaDto])] = Some(flinkSinkPropertiesDlq -> { (q, w) => serializeToBytes[OutDtoWithErrors[JsValue]](q, w) })
      val value2: DataStream[UaspAndKafkaKey] = ds.processWithMaskedDqlFC(
        serviceDataDto,
        convertServiceV,
        sinkDlqProperty,
        producerFactory[KafkaDto]
      )
      ds.process(convertServiceV).getSideOutput(convertServiceV.dlqOutPut).print()
      value2.print()

      value2.maskedProducerF(serviceDataDto,
        flinkSinkPropertiesOK,
        { (u, m) => u.serializeToBytes(None)
        }
        ,
        None,
        producerFactory[KafkaDto]
      )
    }


    pipeRun(data, flinkPipe)

    val dtoes = topicDataArray[KafkaDto](flinkSinkPropertiesDlq)
    val dtoes1 = topicDataArray[KafkaDto](flinkSinkPropertiesOK)
    val dlq = dtoes
      .map(d => Json.parse(new String(d.value)).validate[OutDtoWithErrors[JsValue]].get
      )


    val ok = dtoes1
      .map(d => {
        val str = new String(d.value)
        Json.parse(str).validate[UaspAndKafkaKey].get
      }
      )

    TestResult(ok, dlq)
  }

  "return empty  list " should "be sen to dlq" in {
    val res = rp(List(InputMessageType("123", "{}".getBytes())),
      List()
    )
    assert(res.ok.isEmpty)
    assert(res.dlq.nonEmpty)
    res.dlq
      .foreach(d => assert(OutDtoWithErrors(serviceDataDto, Some("ru.vtb.uasp.inputconvertor.service.UaspDtoConvertService"), List("list is nill"), Some(new JsObject(Map()))) == d))
  }

  "return error parsing " should "be sen to dlq" in {
    val res = rp(List(InputMessageType("123", "{".getBytes())),
      List()
    )
    assert(res.ok.isEmpty)
    assert(res.dlq.nonEmpty)
    val value = OutDtoWithErrors(serviceDataDto, Some("ru.vtb.uasp.inputconvertor.service.UaspDtoConvertService"), List(
      """Error json parsing: Unexpected end-of-input: expected close marker for Object (start marker at [Source: (String)"{"; line: 1, column: 1])
        | at [Source: (String)"{"; line: 1, column: 2], with allProps: Map()""".stripMargin), None)

    res.dlq.foreach { d => {
      assert(d.data == value.data)
      assert(d.errorPosition == value.errorPosition)
      d.errors.foreach(a => assert(a.contains("Error json parsing: Unexpected end-of-input: expected close marker for Object (start marker at [Source: (String)")))
    }

    }


  }

  "return non empty list " should "be sen to ok" in {

    val res = rp(List(InputMessageType("123", "{}".getBytes())),
      List(JsSuccess(uaspDto))
    )
    assert(res.ok.nonEmpty)
    assert(res.dlq.isEmpty)
    res.ok
      .foreach(d => assert(uaspDto == d.uaspDto))
  }

  "return non empty list " should "be sen to ok and dlq" in {

    val value = "Some Error"
    val dto = uaspDto.copy(id = "qwerty")
    val kafkaKey = "123"
    val res = rp(List(InputMessageType(kafkaKey, "{}".getBytes())),
      List(JsSuccess(uaspDto), JsSuccess(dto), JsError(value), JsError(s"${value}1"))
    )
    assert(res.ok.size == 2)
    assert(res.dlq.size == 2)

    assert(res.ok.contains(UaspAndKafkaKey(kafkaKey, uaspDto)))
    assert(res.ok.contains(UaspAndKafkaKey(kafkaKey, dto)))

    assert(res.dlq.contains(OutDtoWithErrors(serviceDataDto, Some("ru.vtb.uasp.inputconvertor.service.UaspDtoConvertService"), List("JsPath error =>Some Error"), Some(new JsObject(Map.empty)))))
    assert(res.dlq.contains(OutDtoWithErrors(serviceDataDto, Some("ru.vtb.uasp.inputconvertor.service.UaspDtoConvertService"), List(s"JsPath error =>Some Error1"), Some(new JsObject(Map.empty)))))

  }


}

object UaspDtoConvertServiceTest {
  val uaspDto = UaspDto("1", Map.empty, Map.empty, Map.empty, Map.empty, Map.empty, Map.empty, Map.empty, "uuid", 0)

  def serviceDataDto = ServiceDataDto("1", "2", "3")

  def producerProps(topicName: String) = {
    val properties = new Properties()
    properties.putAll(Map("bootstrap.servers" -> "bootstrap.servers").asJava)

    val kafkaPrdProperty = KafkaPrdProperty(properties)
    FlinkSinkProperties(topicName, kafkaPrdProperty, None, None, None)
  }

  val flinkSinkPropertiesDlq = producerProps("DLQ")
  val flinkSinkPropertiesOK = producerProps("OK")


}

case class MockUaspDtoParser(v: List[JsResult[UaspDto]]) extends UaspDtoParser {
  override val propsModel: InputPropsModel = InputPropsModel(serviceDataDto, "mdm", null, null, null, false, "", None, 1, None)

  override def fromJValue(mes: JsValue, dtoMap: Map[String, Array[String]]): List[JsResult[UaspDto]] = v
}

class MockDroolsValidator extends DroolsValidator("way4" + "-validation-rules.drl") {
  override def validate(model: List[Any]): List[ValidateMsg] = List()
}

case class TestResult(ok: List[UaspAndKafkaKey], dlq: List[OutDtoWithErrors[JsValue]])