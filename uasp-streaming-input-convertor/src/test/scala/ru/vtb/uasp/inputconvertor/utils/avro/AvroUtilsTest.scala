package ru.vtb.uasp.inputconvertor.utils.avro

import com.sksamuel.avro4s.{AvroSchema, ScalePrecision}
import io.qameta.allure.{Allure, Feature}
import io.qameta.allure.scalatest.AllureScalatestContext
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.constants.BigDecimalConst
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.service.ConvertOutMapService

@Feature("AvroUtilsTest")
class AvroUtilsTest extends AnyFlatSpec with should.Matchers {

  implicit val sp: ScalePrecision = ScalePrecision(BigDecimalConst.SCALE, BigDecimalConst.PRECISION)

  "The serialized / deserialized UaspDto" should "be same" in new AllureScalatestContext {
    Allure.link("302198", "manual", "")
    Allure.tms("18", "")

    val uaspDto = UaspDtostandardFactory("way4").getstandardUaspDto
    val avroSchema = AvroSchema[UaspDto]

    val convertOutMapService = new ConvertOutMapService
    val serialized: Array[Byte] = convertOutMapService.serialize(uaspDto)._2
    // val serialized: Array[Byte] = AvroUtils.avroSerialize[UaspDto](uaspDto, avroSchema)
    val deserialized = AvroUtils.avroDeserialize[UaspDto](serialized, avroSchema) // .avroDeserialize[UaspDto](serialized)
    //avroDeserialize[UaspDto](serialized,schema)

    assert(deserialized == uaspDto)

  }
}
