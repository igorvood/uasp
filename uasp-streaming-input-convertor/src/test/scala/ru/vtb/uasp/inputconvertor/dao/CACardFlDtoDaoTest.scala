package ru.vtb.uasp.inputconvertor.dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json.Json
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.CommonMsgAndProps.jsValueByType
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

class CACardFlDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The result UaspDto" should "be valid" in {

    val allProps: InputPropsModel = new InputPropsModel(
      serviceData = null,
      uaspdtoType = "ca-cardfl",
      consumerProp = null,
      outputSink = null,
      dlqSink = null,
      readSourceTopicFromBeginning = true,
      sha256salt = "",
      messageJsonPath = None,
      1,
      jsonSplitElement = None)


    val commonMessage = jsValueByType(allProps.uaspdtoType)

    val head = allProps.uaspDtoParser.fromJValue(commonMessage, allProps.dtoMap).head
    val uaspDto: UaspDto = Json.fromJson[UaspDto](head.get).get

    val standardUaspDto: UaspDto = UaspDtostandardFactory(allProps.uaspdtoType).getstandardUaspDto(uaspDto.uuid).copy(process_timestamp = uaspDto.process_timestamp, dataBoolean = Map("is_deleted" -> true))

    assert(uaspDto == standardUaspDto)
  }

}





