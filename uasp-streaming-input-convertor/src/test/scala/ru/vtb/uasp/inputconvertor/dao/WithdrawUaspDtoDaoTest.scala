package ru.vtb.uasp.inputconvertor.dao

import io.qameta.allure.Feature
import io.qameta.allure.scalatest.AllureScalatestContext
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.config.ConfigUtils.{getAllProps, getPropsFromResourcesFile, getSchemaKey, getStringFromResourceFile}
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.WithdrawUaspDtoDaoTest.getCommonMessageAndProps
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.factory.{UaspDtoParser, UaspDtoParserFactory}
import ru.vtb.uasp.inputconvertor.service.MsgCollector
import ru.vtb.uasp.inputconvertor.service.TransformHelper.extractJson
import ru.vtb.uasp.inputconvertor.utils.config.{InputPropsModel, NewInputPropsModel}
import ru.vtb.uasp.validate.DroolsValidator

@Feature("WithdrawWay4UaspDtoDaoTest")
class WithdrawUaspDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The result UaspDto" should "be valid" in new AllureScalatestContext {

    val (commonMessage, _, uaspDtoType, dtoMap, _) = getCommonMessageAndProps()
    println("commonMessage: " + commonMessage)
    val uaspDtoParser: UaspDtoParser = UaspDtoParserFactory(uaspDtoType, null)
    val uaspDto: UaspDto = uaspDtoParser.fromJValue(commonMessage.json_message.get, dtoMap)
    println("uaspDto: " + uaspDto)
    val standardUaspDto: UaspDto = UaspDtostandardFactory("way4").getstandardUaspDto
    val expecteduaspDto = standardUaspDto.copy(id = "1353423167",
      dataLong = Map("updatedAt" -> 1652855471872L),
      dataDecimal = Map("sourceSumRub" -> 1.00),
      dataString = Map("operationCode" -> "9775",
        "eventType" -> "ORDER_PROCESSED", "senderMdmId" -> "1353423167",
        "transferOrderId" -> "0eb52d92-d5a3-44a9-9d0a-c2a35508bef8", "senderName" -> "Чистяков Сергей Нколаевич",
        "sourceAccount" -> "40817810223004012753", "interactionChannel" -> "MOBILE", "receiverName" -> "Чистяков Сергей Нколаевич",
        "targetAccount" -> "40817810123004013328"))
    assert(expecteduaspDto == uaspDto.copy(process_timestamp = 0,

      dataString = uaspDto.dataString - ("card_ps_funding_source", "card_masked_pan", "transaction_currency")))
  }
}

object WithdrawUaspDtoDaoTest {
  def getCommonMessageAndProps(args: Array[String] = Array[String]()): (CommonMessageType, NewInputPropsModel, String, Map[String, Array[String]], DroolsValidator) = {
//    val allProps = getAllProps(args, "application-withdraw.properties")
    val allProps:NewInputPropsModel = null
    println(allProps)
    val uaspDtoType = allProps.appUaspdtoType//("app.uaspdto.type")
    println("uaspDtoType: " + uaspDtoType)


    val jsonMessageStr = getStringFromResourceFile(uaspDtoType + "-test.json")
    //val jsonMessageStr = getStringFromResourceFile ( "way4-ift-mes1.json" )
    println("jsonMessageStr: " + jsonMessageStr)

    val inMessage = InputMessageType(message_key = "123", message = jsonMessageStr.getBytes, Map[String, String]())
    println("inMessage: " + inMessage)
    val msgCollector = new MsgCollector
    extractJson(inMessage, allProps,  msgCollector)
    val uaspDtoMap = Map[String, String]() ++ getPropsFromResourcesFile(uaspDtoType + "-uaspdto.properties").get
    val dtoMap = uaspDtoMap.map(m => (m._1, m._2.split("::")))
    (msgCollector.getAll().get(0), allProps, uaspDtoType, dtoMap, new DroolsValidator(uaspDtoType + "-validation-rules.drl"))
  }
}

