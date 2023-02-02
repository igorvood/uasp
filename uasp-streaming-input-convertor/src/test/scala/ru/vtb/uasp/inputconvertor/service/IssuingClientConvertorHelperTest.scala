package ru.vtb.uasp.inputconvertor.service

import com.sksamuel.avro4s.ScalePrecision
import org.scalatest.Ignore
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.constants.BigDecimalConst
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.config.ConfigUtils.getStringFromResourceFile
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.Way4UaspDtoDaoTest
import ru.vtb.uasp.inputconvertor.entity.CommonMessageType
import ru.vtb.uasp.inputconvertor.utils.avro.AvroUtils
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

//FIXME
@Ignore
class IssuingClientConvertorHelperTest extends AnyFlatSpec with should.Matchers {

  implicit val sp: ScalePrecision = ScalePrecision(BigDecimalConst.SCALE, BigDecimalConst.PRECISION)
  "The test data" should "be equals standard way4 UaspDto instance" in {
    val (commonMessage, allProps, uaspDtoType, dtoMap, droolsValidator) = Way4UaspDtoDaoTest.getCommonMessageAndProps()
    println("commonMessage: " + commonMessage)
    val jsonSchema: String = getStringFromResourceFile("schemas/jsonschema-" + uaspDtoType + ".json")
    val enrichedCommonMessage: CommonMessageType = commonMessage.copy(json_schema = Some(jsonSchema))
    val propsModel: InputPropsModel = null

    val testedMessage: CommonMessageType = ConvertHelper.validAndTransform(enrichedCommonMessage, propsModel, droolsValidator, dtoMap)
    println("testedMessage: " + testedMessage)

    val dto = AvroUtils.avroDeserialize[UaspDto](testedMessage.avro_message.get)
    val initialUaspDto: UaspDto = dto.copy(
      process_timestamp = 0)
    //standard
    val standardUaspDto: UaspDto = UaspDtostandardFactory("way4").getstandardUaspDto(initialUaspDto.uuid)
    val expectedUaspDto = standardUaspDto.copy(dataString = standardUaspDto.dataString +
      ("source_system_w4" -> "WAY4", "card_masked_pan" -> "529938******8812", "source_account_w4" -> "40914810200009000369",
        "base_currency_w4" -> "RUR", "card_ps_funding_source" -> "Credit", "transaction_currency" -> "RUR",
        "card_expire_w4" -> "2607", "payment_scheme_w4" -> "Mastercard", "processing_date_string" -> "2021-07-18T18:12:24Z"),
      dataDecimal = standardUaspDto.dataDecimal + ("base_amount_w4" -> -2300.00000))
    println("expected: " + standardUaspDto)
    println("reality : " + initialUaspDto)
    assert(expectedUaspDto == initialUaspDto)
  }
}

