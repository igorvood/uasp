package ru.vtb.uasp.inputconvertor.dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json.Json
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.CommonMsgAndProps.jsValueByType
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

class MDMProfileUaspDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The test data" should "be equals standard mdm-profile UaspDto instance" in {

    val allProps: InputPropsModel = new InputPropsModel(
      null,
      "mdm-profile",
      null,
      null,
      null,
      readSourceTopicFromBeginning = true,
      sha256salt = null,
      messageJsonPath = None, 1,
      jsonSplitElement = Some("contact"))

    val commonMessage = jsValueByType(allProps.uaspdtoType)

    val list = allProps.uaspDtoParser.fromJValue(commonMessage, allProps.dtoMap)

    val value1 = list.head.get
    val uaspDto: UaspDto = Json.fromJson[UaspDto](value1).get


    val standardUaspDto = UaspDtostandardFactory("mdm").getstandardUaspDto(uaspDto.uuid).copy(dataBoolean = Map("is_deleted" -> true))
    val expectedUaspDto = standardUaspDto.copy(id = "456", dataString = Map("local_id" -> "456", "global_id" -> "10324", "system_number" -> "99995"), process_timestamp = uaspDto.process_timestamp)
    assert(expectedUaspDto == uaspDto)
  }
}
