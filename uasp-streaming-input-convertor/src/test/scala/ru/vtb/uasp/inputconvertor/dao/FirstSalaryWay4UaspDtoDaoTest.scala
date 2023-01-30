package ru.vtb.uasp.inputconvertor.dao

import io.qameta.allure.Feature
import io.qameta.allure.scalatest.AllureScalatestContext
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.utils.config.ConfigUtils.{getAllProps, getPropsFromResourcesFile, getSchemaKey, getStringFromResourceFile}
import ru.vtb.uasp.inputconvertor.UaspDtostandardFactory
import ru.vtb.uasp.inputconvertor.dao.FirstSalaryWay4UaspDtoDaoTest.getCommonMessageAndProps
import ru.vtb.uasp.inputconvertor.entity.{CommonMessageType, InputMessageType}
import ru.vtb.uasp.inputconvertor.factory.{UaspDtoParser, UaspDtoParserFactory}
import ru.vtb.uasp.inputconvertor.service.MsgCollector
import ru.vtb.uasp.inputconvertor.service.TransformHelper.extractJson
import ru.vtb.uasp.validate.DroolsValidator

@Feature("FirstSalaryWay4UaspDtoDaoTest")
class FirstSalaryWay4UaspDtoDaoTest extends AnyFlatSpec with should.Matchers {

  "The result UaspDto" should "be contains fields account_number and baseamount_currency" in new AllureScalatestContext {

    val (commonMessage, _, uaspDtoType, dtoMap, _) = getCommonMessageAndProps()
    println("commonMessage: " + commonMessage)
    val uaspDtoParser: UaspDtoParser = UaspDtoParserFactory(uaspDtoType, null)
    val uaspDto: UaspDto = uaspDtoParser.fromJValue(commonMessage.json_message.get, dtoMap)
    println("uaspDto: " + uaspDto)
    val standardUaspDto: UaspDto = UaspDtostandardFactory("way4").getstandardUaspDto
    val expecteduaspDto = standardUaspDto.copy(id = "1493661370",
      dataLong = standardUaspDto.dataLong + ("transaction_datetime" -> 1655828503000L, "processing_datetime" -> 1655817703000L, "effective_date" -> 1655251200000L),
      dataDecimal = Map("transaction_amount" -> 1000.00, "base_amount_w4" -> -1000.00),
      dataString = standardUaspDto.dataString + ("source_system_w4" -> "WAY4", "source_account_w4" -> "40817810569166560",
        "base_currency_w4" -> "RUR", "audit_rrn" -> "217200452175", "operation_id" -> "A95691218370G569166560", "mcc" -> "6051",
        "audit_srn" -> "M1724298H6H1", "service_type" -> "J6", "audit_auth_code" -> "500005", "local_id" -> "1493661370",
        "terminal_type" -> "ECOMMERCE" ,"merchant_name_w4" -> "Visa Unique" , "processing_date_string" -> "2022-06-21T13:21:43Z", "terminal_id" -> "10000015"))
    assert(expecteduaspDto == uaspDto.copy(process_timestamp = 0,
      uuid = "",
      dataString = uaspDto.dataString - ("card_ps_funding_source", "card_masked_pan", "transaction_currency")))
  }
}

object FirstSalaryWay4UaspDtoDaoTest {
  def getCommonMessageAndProps(args: Array[String] = Array[String]()): (CommonMessageType, Map[String, String], String, Map[String, Array[String]], DroolsValidator) = {
    val allProps = getAllProps(args, "application-first-salary-way4.properties")
    println(allProps)
    val uaspDtoType = allProps("app.uaspdto.type")
    println("uaspDtoType: " + uaspDtoType)

    val defaultJsonSchemaKey = getSchemaKey(allProps)
    println("defaultJsonSchemaKey: " + defaultJsonSchemaKey)

    val jsonMessageStr = getStringFromResourceFile(uaspDtoType + "-test.json")
    //val jsonMessageStr = getStringFromResourceFile ( "way4-ift-mes1.json" )
    println("jsonMessageStr: " + jsonMessageStr)

    val inMessage = InputMessageType(message_key = "123", message = jsonMessageStr.getBytes, Map[String, String]())
    println("inMessage: " + inMessage)
    val msgCollector = new MsgCollector
    extractJson(inMessage, allProps, defaultJsonSchemaKey, msgCollector)
    val uaspDtoMap = Map[String, String]() ++ getPropsFromResourcesFile(uaspDtoType + "-uaspdto.properties").get
    val dtoMap = uaspDtoMap.map(m => (m._1, m._2.split("::")))
    (msgCollector.getAll().get(0), allProps, uaspDtoType, dtoMap, new DroolsValidator(uaspDtoType + "-validation-rules.drl"))
  }
}


