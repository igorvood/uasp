package ru.vtb.uasp.inputconvertor.dao

import io.qameta.allure.Feature
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json.{JsResult, JsSuccess, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.dao.CommonMsgAndProps.jsValueByType
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel
import ru.vtb.uasp.inputconvertor.{UaspDtostandard, UaspDtostandardFactory}

@Feature("CurrencyUaspDtoDaoTest")
class CurrencyUaspDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The test data" should "be equals standard currency UaspDto instance" in {

    val allProps: InputPropsModel = new InputPropsModel(
      serviceData = null,
      uaspdtoType = "currency",
      consumerProp = null,
      outputSink = null,
      dlqSink = null,
      readSourceTopicFromBeginning = true,
      sha256salt = "",
      messageJsonPath = None,
      1,
      jsonSplitElement = Some("rates"))

    val commonMessage = jsValueByType(allProps.uaspdtoType)
    val list1 = allProps.uaspDtoParser.fromJValue(commonMessage, allProps.dtoMap)
    val list: List[JsResult[UaspDto]] = list1.map(s => Json.fromJson[UaspDto](s.get))

    val dtoes: List[UaspDto] = list.collect { case d: JsSuccess[UaspDto] => d.value }

    assert(list.size == dtoes.size)

    dtoes.foreach(d => assert(d.id == dtoes.head.id))

    val dtostandard: UaspDtostandard = UaspDtostandardFactory("currency")

    val dtoes1 = dtoes.filter(q => q.dataString.get("rates_currency_alphaCode").contains("AUD")).head


    val expected: UaspDto = dtostandard.getstandardUaspDto(dtoes1.uuid).copy(process_timestamp = dtoes1.process_timestamp)
    assert(expected == dtoes1)

  }

}


