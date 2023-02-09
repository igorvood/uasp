package ru.vtb.uasp.common.abstraction

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.DataStream
import org.scalatest.flatspec.AnyFlatSpec
import play.api.libs.json.Format.GenericFormat
import play.api.libs.json.OFormat.oFormatFromReadsAndOWrites
import ru.vtb.uasp.common.abstraction.MiniPipeLineTrait.valuesTestDataDto
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.service.dto.{KafkaDto, OutDtoWithErrors, ServiceDataDto}
import ru.vtb.uasp.common.utils.config.kafka.KafkaPrdProperty

import java.util.Properties
import scala.collection.JavaConverters.mapAsJavaMapConverter

class NewFlinkStreamPredefTest extends AnyFlatSpec with MiniPipeLineTrait with Serializable {

  val serviceDataDto = ServiceDataDto("1", "2", "3")

  def error[IN](in: Some[IN])= OutDtoWithErrors[IN](serviceDataDto, Some(this.getClass.getName),List("test error"), in)

  private val dlqProcessFunctionError: DlqProcessFunction[TestDataDto, TestDataDto, OutDtoWithErrors[TestDataDto]] = new DlqProcessFunction[TestDataDto, TestDataDto, OutDtoWithErrors[TestDataDto]] {
    override def processWithDlq(dto: TestDataDto): Either[OutDtoWithErrors[TestDataDto], TestDataDto] = Left(error(Some(dto)))
  }



  "NewFlinkStreamPredef.createProducerWithMetric " should " OK" in {

    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>
      NewFlinkStreamPredef.createProducerWithMetric(ds, serviceData = serviceDataDto, producerProps, producerFactory)
    }

    pipeRun(List(TestDataDto("st1", 12)), flinkPipe)

    assertResult(1)(valuesTestDataDto.size)
    val dtoes = topicDataArray[TestDataDto](producerProps)

    assertResult(1)(dtoes.size)
  }

  "NewFlinkStreamPredef.processAndDlqSinkWithMetric " should " OK" in {

    val flinkPipe: DataStream[TestDataDto] => Unit = { ds =>
      NewFlinkStreamPredef.processAndDlqSinkWithMetric[TestDataDto, TestDataDto](
        ds,
        serviceData = serviceDataDto,
        dlqProcessFunctionError,
        Some(producerProps),
        producerFactory[KafkaDto], None)
    }

    pipeRun(List(TestDataDto("st1", 12)), flinkPipe)

    assertResult(1)(valuesTestDataDto.size)
    val dtoes = topicDataArray[TestDataDto](producerProps)

    assertResult(1)(dtoes.size)
  }



  private def producerProps = {
    val properties = new Properties()
    properties.putAll(Map("bootstrap.servers" -> "bootstrap.servers").asJava)

    val kafkaPrdProperty = KafkaPrdProperty(properties)
    FlinkSinkProperties("topicName", kafkaPrdProperty, None, None, None)
  }


}




