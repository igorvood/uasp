package ru.vtb.uasp.common.service

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.json.{JsonConverter, JsonUtil}

import scala.io.Source


class JsonConverterTest extends AnyFlatSpec with Matchers {

  /** В результате конвертации UASPDto получаем корректный Json */
  "As a result of UaspPDto -> Json" should "be" in {

    val uaspDto: UaspDto = UaspDto("1",
      Map[String, Int](),
      Map[String, Long](),
      Map[String, Float]("test" -> 1.1F),
      Map[String, Double](),
      Map[String, BigDecimal](),
      Map[String, String](),
      Map[String, Boolean](),
      "ab0e687b-1db7-4539-b274-bf1a965ae5f1",
      1627301693728L)

    JsonConverter.modelToJson(uaspDto).toString() should be("{\"test\":1.1,\"id\":\"1\",\"uuid\":\"ab0e687b-1db7-4539-b274-bf1a965ae5f1\",\"process_timestamp\":1627301693728}")

  }

//  "test files" should "be" in {
//
//   JsonUtil.getFieldsForCases("src/test/resources/cases")

//  }
}