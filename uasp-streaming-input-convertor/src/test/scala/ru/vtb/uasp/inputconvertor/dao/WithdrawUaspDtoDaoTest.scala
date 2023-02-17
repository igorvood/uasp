package ru.vtb.uasp.inputconvertor.dao

import io.qameta.allure.Feature
import io.qameta.allure.scalatest.AllureScalatestContext
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.config.ConfigUtils.getStringFromResourceFile
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.WithdrawUaspDtoDaoTest.getCommonMessageAndProps
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.service.TransformHelper.extractJson
import ru.vtb.uasp.inputconvertor.utils.config.InputPropsModel

@Feature("WithdrawWay4UaspDtoDaoTest")
class WithdrawUaspDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The result UaspDto" should "be valid" in new AllureScalatestContext {

    val (commonMessage, allProp) = getCommonMessageAndProps()

    val uaspDto: UaspDto = allProp.uaspDtoParser.fromJValue(commonMessage.json_message, allProp.dtoMap)
    val standardUaspDto: UaspDto = UaspDtostandardFactory("way4").getstandardUaspDto(uaspDto.uuid)
    private val dto: UaspDto = uaspDto.copy(process_timestamp = 0,
      dataString = uaspDto.dataString - ("card_ps_funding_source", "card_masked_pan", "transaction_currency"))

    val expecteduaspDto = standardUaspDto.copy(id = "1353423167",
      dataLong = Map("updatedAt" -> 1652855471872L),
      dataDecimal = Map("sourceSumRub" -> 1.00),
      dataString = Map("operationCode" -> "9775",
        "eventType" -> "ORDER_PROCESSED", "senderMdmId" -> "1353423167",
        "transferOrderId" -> "0eb52d92-d5a3-44a9-9d0a-c2a35508bef8", "senderName" -> "Чистяков Сергей Нколаевич",
        "sourceAccount" -> "40817810223004012753", "interactionChannel" -> "MOBILE", "receiverName" -> "Чистяков Сергей Нколаевич",
        "targetAccount" -> "40817810123004013328"), process_timestamp = dto.process_timestamp)
    assert(expecteduaspDto == dto)
  }
}

object WithdrawUaspDtoDaoTest {
  def getCommonMessageAndProps(args: Array[String] = Array[String]()): (CommonMessageType, InputPropsModel) = {
    val allProps: InputPropsModel = new InputPropsModel(
      serviceData = null,
      uaspdtoType = "withdraw",
      consumerProp = null,
      outputSink = null,
      dlqSink = null,
      readSourceTopicFromBeginning = true,
      sha256salt = null,
      messageJsonPath = None,
      jsonSplitElement = None)
    val uaspDtoType = allProps.uaspdtoType

    val jsonMessageStr = getStringFromResourceFile(uaspDtoType + "-test.json")

    val inMessage = InputMessageType(message_key = "123", message = jsonMessageStr.getBytes)
    val msgCollector = new MsgCollector
    extractJson(inMessage, allProps, msgCollector)
    (msgCollector.getAll().get(0).right.get, allProps)
  }
}

