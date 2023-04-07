package ru.vtb.uasp.inputconvertor.dao

import io.qameta.allure.Feature
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json.{JsSuccess, Json}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.CommonMsgAndProps.jsValueByType
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

@Feature("MDMUaspDtoDaoTest")
class MDMUaspDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The test data" should "be equals standard mdm UaspDto instance" in {

    val allProps: InputPropsModel = new InputPropsModel(
      serviceData = null,
      uaspdtoType = "mdm",
      consumerProp = null,
      outputSink = null,
      dlqSink = null,
      readSourceTopicFromBeginning = true,
      sha256salt = "",
      messageJsonPath = None, 1,
      jsonSplitElement = Some("contact"))

    val commonMessage = jsValueByType(allProps.uaspdtoType)

    val list = allProps.uaspDtoParser.fromJValue(commonMessage, allProps.dtoMap)

    val dtoes = list.map(s => Json.fromJson[UaspDto](s.get))

    val uaspDto = dtoes.collect { case d: JsSuccess[UaspDto] => d.value }.head

    val standardUaspDto = UaspDtostandardFactory("mdm").getstandardUaspDto(uaspDto.uuid).copy(dataBoolean = Map("is_deleted" -> true))
    assert(uaspDto.copy(process_timestamp = standardUaspDto.process_timestamp) == standardUaspDto)
  }
}
