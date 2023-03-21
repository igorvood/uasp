package ru.vtb.uasp.inputconvertor.dao

import io.qameta.allure.Feature
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.CommonMsgAndProps.jsValueByType
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

@Feature("Way4UaspDtoDaoTest")
class Way4UaspDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The test data" should "be equals standard way4 UaspDto instance" in {

    val allProp: InputPropsModel = new InputPropsModel(
      serviceData = null,
      uaspdtoType = "way4",
      consumerProp = null,
      outputSink = null,
      dlqSink = null,
      readSourceTopicFromBeginning = true,
      sha256salt = null,
      messageJsonPath = None, 1,
      jsonSplitElement = None)

    val commonMessage = jsValueByType(allProp.uaspdtoType)

    val uaspDto: UaspDto = allProp.uaspDtoParser.fromJValue(commonMessage, allProp.dtoMap).head.get
    val dto: UaspDto = uaspDto.copy(
      dataString = uaspDto.dataString - ("card_ps_funding_source", "transaction_currency", "card_masked_pan",
        "source_account_w4", "base_currency_w4", "source_system_w4"),
      dataDecimal = uaspDto.dataDecimal - "base_amount_w4")

    val standardUaspDto: UaspDto = UaspDtostandardFactory("way4").getstandardUaspDto(uaspDto.uuid)
    assert(standardUaspDto.copy(dataString = standardUaspDto.dataString + ("card_expire_w4" -> "2607", "payment_scheme_w4" -> "Mastercard",
      //      "processing_date_string" -> "2021-07-18T18:12:24Z",
      "terminal_id" -> "11111111"), process_timestamp = dto.process_timestamp)
      == dto)
  }
}
