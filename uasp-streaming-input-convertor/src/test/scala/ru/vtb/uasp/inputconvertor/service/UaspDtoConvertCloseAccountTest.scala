package ru.vtb.uasp.inputconvertor.service

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.DataStream
import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json._
import ru.vtb.uasp.common.abstraction.FlinkStreamProducerPredef.StreamFactory
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.mask.dto.{JsMaskedPath, JsMaskedPathError}
import ru.vtb.uasp.common.service.JsonConvertOutService.{JsonPredef, serializeToBytes}
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors, PropertyWithSerializer, ServiceDataDto}
import ru.vtb.uasp.common.test.MiniPipeLineTrait
import ru.vtb.uasp.common.utils.config.kafka.KafkaPrdProperty
import ru.vtb.uasp.inputconvertor.entity.InputMessageType
import ru.vtb.uasp.inputconvertor.factory.UaspDtoParser
import ru.vtb.uasp.inputconvertor.service.UaspDtoConvertCloseAccountTest._
import ru.vtb.uasp.inputconvertor.service.dto.JsValueAndKafkaKey
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

import java.util.Properties

class UaspDtoConvertCloseAccountTest extends AnyFlatSpec with MiniPipeLineTrait with Serializable {

  private lazy val model = new InputPropsModel(
    serviceDataDto, "close_account",
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

  "return non empty  list " should "be sen to out " in {

    val jsonStr =
      """{
        |  "mdmCode": "10234576",
        |  "productAS": "1544",
        |  "accId": "0192837465",
        |  "accAbs": "CFT",
        |  "initAS": "1386",
        |  "baseApp": 1,
        |  "baseAppText": "Заявление клиента",
        |  "idApp": "CF000000000000000000015550624095",
        |  "dateApp": "2021-09-30"
        |}""".stripMargin

    val res = rp(List(InputMessageType("123", jsonStr.stripMargin.getBytes()))
    )
    val ok = res.ok
    val dlq = res.dlq
    assert(ok.nonEmpty)
    assert(dlq.isEmpty)


  }

}

object UaspDtoConvertCloseAccountTest {

  private def serviceDataDto = ServiceDataDto("1", "2", "3")

  private def producerProps(topicName: String) = {
    val kafkaPrdProperty: KafkaPrdProperty = prdProp
    FlinkSinkProperties(topicName, kafkaPrdProperty, None, None, None)
  }

  private def prdProp = {
    val properties = new Properties()
    properties.put("bootstrap.servers", "bootstrap.servers")

    val kafkaPrdProperty = KafkaPrdProperty(properties)
    kafkaPrdProperty
  }

  private val flinkSinkPropertiesDlq = producerProps("DLQ")
  private val flinkSinkPropertiesOK = producerProps("OK")

}

