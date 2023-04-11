package ru.vtb.uasp.inputconvertor.dao

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import play.api.libs.json.Json
import ru.vtb.uasp.inputconvertor.dao.CommonMsgAndProps.jsValueByType
import ru.vtb.uasp.inputconvertor.dao.dto.CloseAccountDto
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

class CloseAccountDaoTest extends AnyFlatSpec with should.Matchers {

  val allProp: InputPropsModel = new InputPropsModel(
    serviceData = null,
    uaspdtoType = "close_account",
    consumerProp = null,
    outputSink = null,
    dlqSink = null,
    readSourceTopicFromBeginning = true,
    sha256salt = "",
    messageJsonPath = None,
    1,
    jsonSplitElement = None)


  "The result closeAccountDtoExpect" should "be valid" in {

    val  jsonStr = """{
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

    val jsonValue = Json.parse(jsonStr)

    val value1 = allProp.uaspDtoParser.fromJValue(jsonValue, allProp.dtoMap).head.get
    val closeAccountDto = Json.fromJson[CloseAccountDto](value1).get
    val closeAccountDtoExpect = CloseAccountDto("10234576", "1544", "1386", "2021-09-30")

    assert(closeAccountDtoExpect == closeAccountDto)
  }

}
