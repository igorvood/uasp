package aggregate.dao

import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.should.Matchers
import ru.vtb.bevent.first.salary.aggregate.dao.AggregateDao
import ru.vtb.bevent.first.salary.aggregate.entity.{CountsAggregate, CountsType, PaymentInfo}
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.state.mock.MapStateMock


class AggregateDaoTest extends org.scalatest.flatspec.AnyFlatSpec with Matchers with BeforeAndAfter {
  "AggregateDao.getValidDates" should " return Seq(PaymentInfo(Some(0),3), PaymentInfo(Some(0),4) " in {
    AggregateDao.getValidDates(Seq(PaymentInfo(Some(0), 1), PaymentInfo(Some(0), 2), PaymentInfo(Some(0), 3), PaymentInfo(Some(0), 4))) shouldBe Seq(PaymentInfo(Some(0),3), PaymentInfo(Some(0),4))
  }

  "AggregateDao.getValidDates" should " return Seq.empty " in {
    AggregateDao.getValidDates(Seq.empty) shouldBe Seq.empty
  }

  "AggregateDao.getCountFromList" should " return 3 " in {
    AggregateDao.getCountFromList(CountsAggregate(Some(Seq(PaymentInfo(Some(0), 1), PaymentInfo(Some(0), 2), PaymentInfo(Some(0), 3))), None)) shouldBe 3
  }

  "AggregateDao.getCountFromMap" should " return 2 " in {
    AggregateDao.getCountFromMap("a", Some(Map("a"-> Seq(PaymentInfo(Some(0), 0), PaymentInfo(Some(0), 1))))) shouldBe 2
  }

  "AggregateDao.getCountFromList" should " return 0" in {
    AggregateDao.getCountFromList(CountsAggregate(None, None)) shouldBe 0
    AggregateDao.getCountFromList(CountsAggregate(Some(Seq.empty), None)) shouldBe 0
  }

  "AggregateDao.getCountFromMap" should " return 0 " in {
    AggregateDao.getCountFromMap("ab", Some(Map("a"-> Seq(PaymentInfo(Some(0), 0), PaymentInfo(Some(0), 1))))) shouldBe 0
    AggregateDao.getCountFromMap("ab", None) shouldBe 0
  }

  "AggregateDao.updateState" should " return Seq(PaymentInfo(Some(0),456), PaymentInfo(Some(0),789), PaymentInfo(Some(0),101112) " in {
    val state = new MapStateMock[String, CountsAggregate]()
    AggregateDao.updateState(CountsType.Salary, UaspDto("", Map(), Map("event_dttm" -> 123L), Map(), Map(), Map(), Map(), Map("system_is_update_sal"-> true), "", 123L), state)
    AggregateDao.updateState(CountsType.Salary, UaspDto("", Map(), Map("event_dttm" -> 456L), Map(), Map(), Map(), Map(), Map("system_is_update_sal"-> true), "", 12123L), state)
    AggregateDao.updateState(CountsType.Salary, UaspDto("", Map(), Map("event_dttm" -> 789L), Map(), Map(), Map(), Map(), Map("system_is_update_sal"-> true), "", 12123L), state)
    AggregateDao.updateState(CountsType.Salary, UaspDto("", Map(), Map("event_dttm" -> 101112L), Map(), Map(), Map(), Map(), Map("system_is_update_sal"-> true), "", 12123L), state)

    state.get(CountsType.Salary.toString).dates.get shouldBe Seq(PaymentInfo(Some(0),456), PaymentInfo(Some(0),789), PaymentInfo(Some(0),101112))
  }

  "AggregateDao.updateStateAccount" should " return Seq(PaymentInfo(Some(0),456), PaymentInfo(Some(0),789), PaymentInfo(Some(0),101112) " in {
    val state = new MapStateMock[String, CountsAggregate]()
    AggregateDao.updateStateAccount(CountsType.Account, "a", UaspDto("", Map(), Map("event_dttm" -> 123L), Map(), Map(), Map(), Map(), Map("system_is_update_sal"-> true), "", 123L), state)
    AggregateDao.updateStateAccount(CountsType.Account, "a",UaspDto("", Map(), Map("event_dttm" -> 456L), Map(), Map(), Map(), Map(), Map("system_is_update_sal"-> true), "", 12123L), state)
    AggregateDao.updateStateAccount(CountsType.Account, "a",UaspDto("", Map(), Map("event_dttm" -> 789L), Map(), Map(), Map(), Map(), Map("system_is_update_sal"-> true), "", 12123L), state)
    AggregateDao.updateStateAccount(CountsType.Account, "a",UaspDto("", Map(), Map("event_dttm" -> 101112L), Map(), Map(), Map(), Map(), Map("system_is_update_sal"-> true), "", 12123L), state)
    AggregateDao.updateStateAccount(CountsType.Account, "b",UaspDto("", Map(), Map("event_dttm" -> 234L), Map(), Map(), Map(), Map(), Map("system_is_update_sal"-> true), "", 12123L), state)

    state.get(CountsType.Account.toString).datesWithMap.get("a") shouldBe Seq(PaymentInfo(Some(0),456), PaymentInfo(Some(0),789), PaymentInfo(Some(0),101112))
    state.get(CountsType.Account.toString).datesWithMap.get("b") shouldBe Seq(PaymentInfo(Some(0),234))
  }

  "AggregateDao.getCountAggregateFromState1" should " return CountsAggregate(None, Some(Map(\"a\" -> Seq(PaymentInfo(Some(0), 123L), PaymentInfo(Some(0), 456L)))) " in {
    val state = new MapStateMock[String, CountsAggregate]()

    AggregateDao.getCountAggregateFromState(CountsType.Account, state) shouldBe CountsAggregate(None, None)

    AggregateDao.updateStateAccount(CountsType.Account, "a", UaspDto("", Map(), Map("event_dttm" -> 123L), Map(), Map(), Map(), Map(), Map("system_is_update_sal"-> true), "", 123L), state)
    AggregateDao.updateStateAccount(CountsType.Account, "a",UaspDto("", Map(), Map("event_dttm" -> 456L), Map(), Map(), Map(), Map(), Map("system_is_update_sal"-> true), "", 12123L), state)
    AggregateDao.getCountAggregateFromState(CountsType.Account, state)

    AggregateDao.getCountAggregateFromState(CountsType.Account, state) shouldBe CountsAggregate(None, Some(Map("a" -> Seq(PaymentInfo(Some(0), 123L), PaymentInfo(Some(0), 456L)))))
  }

  "AggregateDao.getCountAggregateFromState" should " return CountsAggregate(None, Some(Map(\"a\" -> Seq(PaymentInfo(Some(0), 123L), PaymentInfo(Some(0), 456L))))" in {
    val state = new MapStateMock[String, CountsAggregate]()

    AggregateDao.getCountAggregateFromState(CountsType.Account, state) shouldBe CountsAggregate(None, None)

    AggregateDao.updateStateAccount(CountsType.Account, "a", UaspDto("", Map(), Map("event_dttm" -> 123L), Map(), Map(), Map(), Map(), Map("system_is_update_sal"-> true), "", 123L), state)
    AggregateDao.updateStateAccount(CountsType.Account, "a",UaspDto("", Map(), Map("event_dttm" -> 456L), Map(), Map(), Map(), Map(), Map("system_is_update_sal"-> true), "", 12123L), state)
    AggregateDao.getCountAggregateFromState(CountsType.Account, state)

    AggregateDao.getCountAggregateFromState(CountsType.Account, state) shouldBe CountsAggregate(None, Some(Map("a" -> Seq(PaymentInfo(Some(0), 123L), PaymentInfo(Some(0), 456L)))))
  }

  "AggregateDao.updateStateAccount(CountsType.FirstSalaryForAccount)" should " return CountsAggregate(None, Some(Map(\"a\" -> Seq(PaymentInfo(Some(300), 123L), PaymentInfo(Some(500), 456L)))))" in {
    val state = new MapStateMock[String, CountsAggregate]()

    AggregateDao.updateStateAccount(CountsType.FirstSalaryForAccount, "a", UaspDto("", Map(), Map("event_dttm" -> 123L), Map(), Map(), Map("amount" -> BigDecimal("300")), Map(), Map("system_is_update_sal"-> true), "", 123L), state)
    AggregateDao.updateStateAccount(CountsType.FirstSalaryForAccount, "a",UaspDto("", Map(), Map("event_dttm" -> 456L), Map(), Map(), Map("amount" -> BigDecimal("500")), Map(), Map("system_is_update_sal"-> true), "", 12123L), state)

    AggregateDao.getCountAggregateFromState(CountsType.FirstSalaryForAccount, state) shouldBe CountsAggregate(None, Some(Map("a" -> Seq(PaymentInfo(Some(300), 123L), PaymentInfo(Some(500), 456L)))))
  }

  "AggregateDao.deleteStateAccount" should " return CountsAggregate(None, Some(Map(\"a\" -> List(), \"b\" -> List(PaymentInfo(Some(500),456)))))" in {
    val state = new MapStateMock[String, CountsAggregate]()

    AggregateDao.updateStateAccount(CountsType.FirstSalaryForAccount, "a", UaspDto("", Map(), Map("event_dttm" -> 123L), Map(), Map(), Map("amount" -> BigDecimal("300")), Map(), Map("system_is_update_sal"-> true), "", 123L), state)
    AggregateDao.updateStateAccount(CountsType.FirstSalaryForAccount, "a",UaspDto("", Map(), Map("event_dttm" -> 456L), Map(), Map(), Map("amount" -> BigDecimal("500")), Map(), Map("system_is_update_sal"-> true), "", 12123L), state)
    AggregateDao.updateStateAccount(CountsType.FirstSalaryForAccount, "b",UaspDto("", Map(), Map("event_dttm" -> 456L), Map(), Map(), Map("amount" -> BigDecimal("500")), Map(), Map("system_is_update_sal"-> true), "", 12123L), state)

    AggregateDao.deleteStateAccount(CountsType.FirstSalaryForAccount, "a", state)
    AggregateDao.getCountAggregateFromState(CountsType.FirstSalaryForAccount, state) shouldBe CountsAggregate(None, Some(Map("a" -> List(), "b" -> List(PaymentInfo(Some(500),456)))))
  }

  "AggregateDao.getSalaryFromMap" should " return List(PaymentInfo(Some(300),123), PaymentInfo(Some(500),456))" in {
    val state = new MapStateMock[String, CountsAggregate]()

    AggregateDao.updateStateAccount(CountsType.FirstSalaryForAccount, "a", UaspDto("", Map(), Map("event_dttm" -> 123L), Map(), Map(), Map("amount" -> BigDecimal("300")), Map(), Map("system_is_update_sal"-> true), "", 123L), state)
    AggregateDao.updateStateAccount(CountsType.FirstSalaryForAccount, "a",UaspDto("", Map(), Map("event_dttm" -> 456L), Map(), Map(), Map("amount" -> BigDecimal("500")), Map(), Map("system_is_update_sal"-> true), "", 12123L), state)

    AggregateDao.getSalaryFromMap("a", state.get(CountsType.FirstSalaryForAccount.toString).datesWithMap) shouldBe List(PaymentInfo(Some(300),123), PaymentInfo(Some(500),456))
  }

  "AggregateDao.deleteStateAccount(Empty state)" should " return CountsAggregate(None,Some(Map(c -> List())))" in {
    val state = new MapStateMock[String, CountsAggregate]()

    AggregateDao.deleteStateAccount(CountsType.FirstSalaryForAccount, "c", state)
    AggregateDao.getCountAggregateFromState(CountsType.FirstSalaryForAccount, state) shouldBe CountsAggregate(None,Some(Map("c" -> List())))
  }

  "AggregateDao.getFirstDaysForLastDays" should " return 123L" in {
    AggregateDao.getFirstDaysForLastDays(Seq(PaymentInfo(Some(BigDecimal("123.145")), 123456L), PaymentInfo(Some(BigDecimal("6789.145")), 123L))) shouldBe 123L
  }

  "AggregateDao.getFirstDaysForLastDays (Empty list)" should " return 0L" in {
    AggregateDao.getFirstDaysForLastDays(Seq.empty) shouldBe 0L
  }

  "AggregateDao.getSumOfSalary" should " return 6912.290" in {
    AggregateDao.getSumOfSalary(Seq(PaymentInfo(Some(BigDecimal("123.145")), 123456L), PaymentInfo(Some(BigDecimal("6789.145")), 123L))) shouldBe BigDecimal("6912.290")
  }

  "AggregateDao.getSumOfSalary (Empty list)" should " return 0L" in {
    AggregateDao.getSumOfSalary(Seq.empty) shouldBe BigDecimal("0")
  }
}
