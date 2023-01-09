package aggregate.dao.drl

import aggregate.dao.abstraction._
import aggregate.dao.drl.BigCurrencyRateTest._
import io.qameta.allure.Feature
import ru.vtb.bevent.first.salary.aggregate.factory.BusinessRules
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.mutator.service.BusinessRulesService

import java.util.{Calendar, Date}


@Feature("ExchangeCurrencyRateTestTest")
class BigCurrencyRateTest extends AbstractDroolsTestCase {

  override protected def drlFileList: List[String] = List("source_account.drl")

  private val businessRules: BusinessRules = BusinessRules(
    level0 = BusinessRulesService(drlFileList),
    level1 = BusinessRulesService(List("level1.drl")),
    level2 = BusinessRulesService(List("level2.drl")),
    cases = BusinessRulesService(List("case_8.drl", "case_29.drl", "case_38.drl", "case_39.drl", "case_44.drl", "case_56.drl", "case_57.drl")),
  )


  "source_account  " should " Mutate " in {


    println(validUaspDtoAccount)

    val dto = businessRules.level0.map(validUaspDtoAccount)

    val dto1 = runBusinessRules(businessRules, dto)


    println(dto1)


  }


  def runBusinessRules(businessRules: BusinessRules, data: UaspDto): UaspDto = {
    val uaspDtoProccessedLevel1 = businessRules.level1.map(data) // BusinessRulesService(props.listOfBusinessRuleLevel1).map(data)
    println(uaspDtoProccessedLevel1)
    val uaspDtoProccessedLevel2 = businessRules.level2.map(uaspDtoProccessedLevel1) // BusinessRulesService(props.listOfBusinessRuleLevel2).map(uaspDtoProccessedLevel1)
    println(uaspDtoProccessedLevel2)
    val uaspDtoProccessed = businessRules.cases.map(uaspDtoProccessedLevel2) // BusinessRulesService(props.listOfBusinessRule).map(uaspDtoProccessedLevel2)
    uaspDtoProccessed
  }

}


object BigCurrencyRateTest {

  def globalId = "globalId"

  def currentTimeForData = new Date().getTime

  def clientId = "clientId"

  protected val validUaspDtoAccount: UaspDto = UaspDto(
    globalId.toString,
    Map("currency_scale" -> 1,
      "COUNT_SAL" -> 19,
    ),
    Map(
      "transaction_dttm" -> currentTimeForData,
      "exchange_dttm" -> currentTimeForData,
      "effective_date" -> currentTimeForData,
      "processing_datetime" -> currentTimeForData,


      "DATE_LAST_SAL" -> 0
    )
    ,
    Map.empty
    ,
    Map.empty
    ,
    Map("base_amount_w4" -> 2000.0, "currency_price" -> 1.0, "transaction_amount" -> 100000.0, "currency_price" -> BigDecimal(26275456828.686384))
    ,
    Map(
      "terminal_type" -> "POS",
      "card_masked_pan" -> "529938******8812",
      "source_account_w4" -> "40914810200009000369",
      "local_id" -> clientId,
      "payment_direction" -> "Debit",
      "action_type" -> "Presentment",
      "global_id" -> globalId.toString,
      "audit_srn" -> "RBXXOZDY7EL6",
      "audit_rrn" -> "1ST63EJBV02D",
      "mcc" -> "4830",
      "exchange_currency" -> "USD",
      "base_currency_w4" -> "USD",
      "source_system_w4" -> "WAY4",
      "service_type" -> "I7",
      "card_ps_funding_source" -> "Credit",
      "processing_result_code" -> "0",
      "processing_resolution" -> "Accepted",
      "system-uasp-way-classification" -> "way4-case-pos,way4-case-5-2",
      "audit_auth_code" -> "364239",
      "operation_id" -> "331699532",
      "transaction_currency" -> "USD",
      "kbo_w4" -> "731500-03"
    )
    ,
    Map.empty
    ,
    java.util.UUID.randomUUID().toString
    ,
    Calendar.getInstance().getTime.getTime
  )

  val someaccount = "SOME_ACCOUNT"

  protected def testData(): List[TestCaseData] = {

    List[TestCaseData](
      TestString("1111111111111", NoneTestAction()),
      TestString("source_account_cft", AddTestAction(someaccount), Some("")),
      TestString("source_account_ca", AddTestAction(someaccount), Some("")),
      TestString("contract_num", AddTestAction(someaccount), Some("")),
      TestString("source_account_w4", AddTestAction(someaccount), Some("")),
      TestString("sourceAccount", AddTestAction(someaccount), Some("")),
    )
  }

}