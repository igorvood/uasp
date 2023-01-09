package aggregate.dao.drl

import aggregate.dao.abstraction._
import aggregate.dao.drl.SourceAccountTest._
import io.qameta.allure.Feature
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.mutator.service.dto.{Add, StringMap}

import scala.util.Try


@Feature("source_account drl Test")
class SourceAccountTest extends AbstractDroolsTestCase {

  override protected def drlFileList: List[String] = List("source_account.drl")

  "source_account  " should " Mutate " in {

    val triedAssertions = testData().map { test =>
      test -> Try {
        val result = runDrools(validUaspDtoAccount, test).toList
        test.expecped.map { q =>
          assertResult(1)(result.size)
          assertResult(result.head.typeField)(StringMap(someaccount))
          assertResult(result.head.nameField)("source_account")
          assertResult(result.head.typeOperation)(Add())
        }.getOrElse(assertResult(0)(result.size))
      }
    }
    assertTry(triedAssertions)
  }

}

object SourceAccountTest {
  protected val validUaspDtoAccount: UaspDto = UaspDto(
    "validUaspId",
    Map(),
    Map(),
    Map(),
    Map(),
    Map(),
    Map(),
    Map(),
    "validUaspUUID",
    13
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



