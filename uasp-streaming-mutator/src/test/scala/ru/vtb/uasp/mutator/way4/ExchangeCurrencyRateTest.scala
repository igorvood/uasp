package ru.vtb.uasp.mutator.way4

import io.qameta.allure.Feature
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.mutator.service.dto.{Add, LongMap, StringMap}
import ru.vtb.uasp.mutator.way4.ConstTest.addKey
import ru.vtb.uasp.mutator.way4.ExchangeCurrencyRateTest._
import ru.vtb.uasp.mutator.way4.abstraction._

import scala.util.Try


@Feature("ExchangeCurrencyRateTestTest")
class ExchangeCurrencyRateTest extends AbstractDroolsTestCase {

  override protected def drlFileList: List[String] = List("exchange_currency_fields.drl")


  "ExchangeCurrencyRate " should " be ok " + addKey in {

    val triedAssertions = testDataCft().map { test =>
      test -> Try {
        val result = runDrools(validUaspDtoCFT, test).toList
        test.expecped.map { q =>
          assertResult(2)(result.size)
          //          result.foreach(d => assertResult("Exchange fields name unification from CFT pipeline")(d.droolsName))
          result.foreach(d => assertResult(Add())(d.typeOperation))
          assertResult(List("exchange_currency", "exchange_dttm").sorted)(result.map(d => d.nameField).sorted)
          assertResult(2)(result.count(d => d.typeField == StringMap("RUR") || d.typeField == StringMap("EUR") || d.typeField == LongMap(1512)))
        }
          .getOrElse(assertResult(0)(result.size))
      }
    }

    assertTry(triedAssertions)
  }

}


object ExchangeCurrencyRateTest {
  val validUaspDtoCFT: UaspDto = UaspDto(
    "validUaspId",
    Map(),
    Map("event_dttm_cft" -> 1512),
    Map(),
    Map(),
    Map("transaction_amount" -> BigDecimal(152)),
    Map("amount_currency_cft" -> "EUR"),
    Map(),
    "validUaspUUID",
    13
  )

  protected def testDataCft(): List[TestCaseData] = List[TestCaseData](
    TestString("amount_currency_cft", NoneTestAction(), Some("")),
    TestString("amount_currency_cft", DeleteTestAction()),
    TestString("amount_currency_cft", AddTestAction("RUB"), Some("")),

    TestLong("event_dttm_cft", DeleteTestAction()),
  )

}