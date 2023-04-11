package ru.vtb.uasp.inputconvertor.service

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.DataStream
import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json._
import ru.vtb.uasp.common.abstraction.FlinkStreamProducerPredef.StreamFactory
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.mask.MaskedPredef.PathFactory
import ru.vtb.uasp.common.mask.MaskedStrPathWithFunName
import ru.vtb.uasp.common.mask.dto.{JsMaskedPath, JsMaskedPathError}
import ru.vtb.uasp.common.service.JsonConvertOutService.{JsonPredef, serializeToBytes}
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors, PropertyWithSerializer, ServiceDataDto}
import ru.vtb.uasp.common.test.MiniPipeLineTrait
import ru.vtb.uasp.common.utils.config.ConfigUtils.getStringFromResourceFile
import ru.vtb.uasp.common.utils.config.kafka.KafkaPrdProperty
import ru.vtb.uasp.inputconvertor.Convertor
import ru.vtb.uasp.inputconvertor.entity.InputMessageType
import ru.vtb.uasp.inputconvertor.factory.UaspDtoParser
import ru.vtb.uasp.inputconvertor.service.DtoConvertServiceNotMockTest.prdProp
import ru.vtb.uasp.inputconvertor.service.DtoConvertServiceTest.{flinkSinkPropertiesDlq, flinkSinkPropertiesOK, serviceDataDto}
import ru.vtb.uasp.inputconvertor.service.dto.JsValueAndKafkaKey
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

import java.util.Properties

class DtoConvertServiceNotMockTest extends AnyFlatSpec with MiniPipeLineTrait with Serializable {

  private lazy val model = new InputPropsModel(
    serviceDataDto, "customer-profile-full",
    null,
    null,
    null,
    false, "",
    None,
    1,
    None)
  private val convertService: (UaspDtoParser) => DtoConvertService = { p =>
    new DtoConvertService(p, model.droolsValidator, ServiceDataDto("1", "2", "3"), model.dtoMap)
  }


  def rp(data: List[InputMessageType]): TestResult = {

    val parser = model.uaspDtoParser

    val convertServiceV = convertService(parser)

    val flinkPipe: DataStream[InputMessageType] => Unit = { ds =>

      val sinkDlqProperty: Option[(FlinkSinkProperties, (OutDtoWithErrors[JsValue], Option[JsMaskedPath]) => Either[List[JsMaskedPathError], KafkaDto])] = Some(flinkSinkPropertiesDlq -> { (q, w) => serializeToBytes[OutDtoWithErrors[JsValue]](q, w) })
      val value2: DataStream[JsValueAndKafkaKey] = ds.processWithMaskedDqlFC(
        serviceDataDto,
        convertServiceV,
        sinkDlqProperty,
        producerFactory[KafkaDto]
      )
      ds.process(convertServiceV).getSideOutput(convertServiceV.dlqOutPut).print()
      value2.print()

      value2.maskedProducerF(serviceDataDto,
        PropertyWithSerializer(flinkSinkPropertiesOK, {
          _.serializeToKafkaJsValue
        }),
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
        Json.parse(str).validate[JsValueAndKafkaKey].get
      }
      )

    TestResult(ok, dlq)
  }

  "return empty  list " should "be sen to dlq" in {

    val jsonMessageStr = getStringFromResourceFile("way4-error-test.json")

    val res = rp(List(InputMessageType("123",
      """{
        |    "contract_id": 123,
        |    "customer_id": "222",
        |    "contract_num": "40817810223004012753"
        |  }""".stripMargin.getBytes()))
    )
    assert(res.ok.isEmpty)
    assert(res.dlq.nonEmpty)


  }

  "return masked error " should "be sen to dlq" in {

    val jsMaskedPath = Iterable(MaskedStrPathWithFunName("data.contract_num", "ru.vtb.uasp.common.mask.fun.CenterMaskService(3,4)")).toJsonPath.right.get


    val properties = FlinkSinkProperties("dlq", prdProp, None, None, Some(jsMaskedPath))
    val model1 = new InputPropsModel(
      serviceDataDto, "customer-profile-full",
      null,
      null,
      properties,
      false, "",
      None,
      1,
      None)

    val flinkPipe: DataStream[InputMessageType] => Unit = { ds =>
      val value = Convertor.process(ds, model1, producerFactory[KafkaDto])

      value.print()

      value.maskedProducerF(serviceDataDto,
        PropertyWithSerializer(flinkSinkPropertiesOK, {
          _.serializeToKafkaJsValue
        }),
        None,
        producerFactory[KafkaDto]
      )
    }


    pipeRun(List(InputMessageType("123",
      """{
        |    "contract_id": 123,
        |    "customer_id": "222",
        |    "contract_num": "40817810223004012753"
        |  }""".stripMargin.getBytes())), flinkPipe)


    val dtoesDlq = topicDataArray[KafkaDto](properties)
    val dtoesOk = topicDataArray[KafkaDto](flinkSinkPropertiesOK)
    val dlq = dtoesDlq
      .map(d => Json.parse(new String(d.value)).validate[OutDtoWithErrors[JsValue]].get
      )


    val ok = dtoesOk
      .map(d => {
        val str = new String(d.value)
        Json.parse(str).validate[JsValueAndKafkaKey].get
      }
      )


    assert(ok.isEmpty)
    assert(dlq.nonEmpty)

    val value = dlq.head.data.get.asInstanceOf[JsObject]
    val contractNum = value.value("contract_num")

    assert(contractNum == JsString("408*************2753"))


  }


}

object DtoConvertServiceNotMockTest {
  val uaspDto = UaspDto("1", Map.empty, Map.empty, Map.empty, Map.empty, Map.empty, Map.empty, Map.empty, "uuid", 0)

  def serviceDataDto = ServiceDataDto("1", "2", "3")

  def producerProps(topicName: String) = {
    val kafkaPrdProperty: KafkaPrdProperty = prdProp
    FlinkSinkProperties(topicName, kafkaPrdProperty, None, None, None)
  }

  private def prdProp = {
    val properties = new Properties()
    properties.put("bootstrap.servers", "bootstrap.servers")

    val kafkaPrdProperty = KafkaPrdProperty(properties)
    kafkaPrdProperty
  }

  val flinkSinkPropertiesDlq = producerProps("DLQ")
  val flinkSinkPropertiesOK = producerProps("OK")

}

