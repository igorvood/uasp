package ru.vtb.bevent.first.salary.aggregate.dao

import org.apache.flink.api.common.state.MapState
import ru.vtb.bevent.first.salary.aggregate.entity.{CountsAggregate, CountsType, PaymentInfo}
import ru.vtb.uasp.common.dto.UaspDto

import java.util.Date
import scala.util.Try


object AggregateDao {
  val salaryUpdateFieldName = "system_is_update_sal"
  val posUpdateFieldName = "system_is_update_pos"
  val pansUpdateFieldName = "system_is_update_pens"
  val accountUpdateFieldName = "system_is_update_ns"

  val accountUpdate48CaseFieldName = "system_is_update_44_case"
  val accountDelete48CaseFieldName = "system_is_clear_state_44_case"

  val sourceAccount = "source_account"


  private val hash_card_number = "hash_card_number"

  def fullStateUpdate(salaryCountAggregateState: MapState[String, CountsAggregate], accountNumber: String, uaspDtoProccessed: UaspDto) = {
    if (uaspDtoProccessed.dataBoolean.contains(salaryUpdateFieldName) && uaspDtoProccessed.dataBoolean(salaryUpdateFieldName)) {
      updateState(CountsType.Salary, uaspDtoProccessed, salaryCountAggregateState)
    } else if (uaspDtoProccessed.dataBoolean.contains(posUpdateFieldName) && uaspDtoProccessed.dataBoolean(posUpdateFieldName)) {
      updateState(CountsType.Pos, uaspDtoProccessed, salaryCountAggregateState)
      uaspDtoProccessed.dataString.get(hash_card_number)
        .foreach { shaCardNum => updateStateAccount(CountsType.PosByCard, shaCardNum, uaspDtoProccessed, salaryCountAggregateState) }
    } else if (uaspDtoProccessed.dataBoolean.contains(pansUpdateFieldName) && uaspDtoProccessed.dataBoolean(pansUpdateFieldName)) {
      updateState(CountsType.Pens, uaspDtoProccessed, salaryCountAggregateState)
    } else if (uaspDtoProccessed.dataBoolean.contains(accountUpdateFieldName) && uaspDtoProccessed.dataBoolean(accountUpdateFieldName)) {
      updateStateAccount(CountsType.Account, accountNumber, uaspDtoProccessed, salaryCountAggregateState)
    }


    if (uaspDtoProccessed.dataBoolean.contains(accountUpdate48CaseFieldName) && uaspDtoProccessed.dataBoolean(accountUpdate48CaseFieldName)) {
      updateStateAccount(CountsType.FirstSalaryForAccount, accountNumber, uaspDtoProccessed, salaryCountAggregateState)
    } else if (uaspDtoProccessed.dataBoolean.contains(accountDelete48CaseFieldName) && uaspDtoProccessed.dataBoolean(accountDelete48CaseFieldName)) {
      deleteStateAccount(CountsType.FirstSalaryForAccount, accountNumber, salaryCountAggregateState)
    }
  }

  def enrichState(salaryCountAggregateState: MapState[String, CountsAggregate], uaspDtoProccessedLevel0: UaspDto) = {

    //First salary

    val listSalary = getCountAggregateFromState(CountsType.Salary, salaryCountAggregateState)
    val countSalary = getCountFromList(listSalary)
    val salaryDates = listSalary.dates.getOrElse(Seq(PaymentInfo(None, 0L))).map(_.date)
    val firstEventDate: Long = salaryDates.min

    val lastEventDate: Long = salaryDates.max
    //Pens
    val listPans = getCountAggregateFromState(CountsType.Pens, salaryCountAggregateState)
    val countPans = getCountFromList(listPans)

    val pansDate = listPans.dates.getOrElse(Seq(PaymentInfo(None, 0L))).map(_.date).max
    //POS
    val listPos = getCountAggregateFromState(CountsType.Pos, salaryCountAggregateState)

    val aggregate = getCountAggregateFromState(CountsType.Pos, salaryCountAggregateState)
      .datesWithMap
      .map(m => m)
    //
    val posByCardPayment = for {
      map <- getCountAggregateFromState(CountsType.PosByCard, salaryCountAggregateState).datesWithMap
      key <- uaspDtoProccessedLevel0.dataString.get(hash_card_number)
      cnt <- map.get(key)
    } yield cnt
    val posByCardCnt = posByCardPayment.map(_.size).getOrElse(0)

    val countPos = getCountFromList(listPos)
    //MultiAccounts
    val mapAccounts = getCountAggregateFromState(CountsType.Account, salaryCountAggregateState).datesWithMap
    val accountNumber = uaspDtoProccessedLevel0.dataString.getOrElse(sourceAccount, "")

    val countAccount = getCountFromMap(accountNumber, mapAccounts)
    //Case 48
    val listSalaryForAccountNumber = getCountAggregateFromState(CountsType.FirstSalaryForAccount, salaryCountAggregateState).datesWithMap
    val accountNumberFor = uaspDtoProccessedLevel0.dataString.getOrElse(sourceAccount, "")
    val firstSalaryForAccount = getSalaryFromMap(accountNumberFor, listSalaryForAccountNumber)

    val validDates = firstSalaryForAccount
      .filter(paymentInfo => filterByDay(paymentInfo.date))
    val amountAll = getSumOfSalary(validDates)

    val firstDaysForLastDays = getFirstDaysForLastDays(validDates)

    val uaspDtoWithState = uaspDtoProccessedLevel0.copy(
      dataInt = uaspDtoProccessedLevel0.dataInt + ("COUNT_PENS" -> countPans, "COUNT_SAL" -> countSalary, "COUNT_POS" -> countPos, "COUNT_POS_CARD" -> posByCardCnt, "COUNT_REP" -> countAccount),
      dataLong = uaspDtoProccessedLevel0.dataLong + ("DATE_LAST_SAL" -> lastEventDate, "DATE_FIRST_SAL" -> firstEventDate, "DATE_LAST_PENS" -> pansDate, "FIRST_DAYS_FOR_LAST_DAYS" -> firstDaysForLastDays), // TODO Change to normal name
      dataDecimal = uaspDtoProccessedLevel0.dataDecimal + ("AMOUNT_ALL" -> amountAll)
    )
    (accountNumber, uaspDtoWithState)
  }

  def updateState(typeEven: CountsType.Count, inMsg: UaspDto, salaryCountAggregateState: MapState[String, CountsAggregate]): Unit = {
    val sliceList = if (salaryCountAggregateState.contains(typeEven.toString)) {
      salaryCountAggregateState
        .get(typeEven.toString)
        .dates
        .map(getValidDates)
        .getOrElse(Seq.empty)
    } else {
      Seq.empty
    }

    val eventTime = if (inMsg.dataLong.contains("event_dttm")) inMsg.dataLong("event_dttm") else inMsg.process_timestamp
    val amount = inMsg.dataDecimal.getOrElse("amount", BigDecimal(0))

    val newState = CountsAggregate(Some(sliceList :+ PaymentInfo(Some(amount), eventTime)), None)

    salaryCountAggregateState.put(typeEven.toString, newState)
  }


  def updateStateAccount(typeEven: CountsType.Count, key: String, inMsg: UaspDto, salaryCountAggregateState: MapState[String, CountsAggregate]): Unit = {
    val sliceList = if (salaryCountAggregateState.contains(typeEven.toString)) {
      salaryCountAggregateState
        .get(typeEven.toString)
        .datesWithMap
        .map(_.getOrElse(key, Seq.empty))
        .map(getValidDates)
        .getOrElse(Seq.empty)
    } else {
      Seq.empty
    }

    val eventTime = if (inMsg.dataLong.contains("event_dttm")) inMsg.dataLong("event_dttm") else inMsg.process_timestamp
    val amount = inMsg.dataDecimal.getOrElse("amount", BigDecimal(0))

    val newState = if (salaryCountAggregateState.contains(typeEven.toString)) {
      salaryCountAggregateState.get(typeEven.toString).datesWithMap.get ++ Map(key -> (sliceList :+ PaymentInfo(Some(amount), eventTime)))
    } else {
      Map(key -> (sliceList :+ PaymentInfo(Some(amount), eventTime)))
    }

    salaryCountAggregateState.put(typeEven.toString, CountsAggregate(None, Some(newState)))
  }

  def getCountAggregateFromState(typeEven: CountsType.Count, salaryCountAggregateState: MapState[String, CountsAggregate]): CountsAggregate = {
    val listSalary = if (salaryCountAggregateState.contains(typeEven.toString)) {
      salaryCountAggregateState.get(typeEven.toString)
    } else {
      CountsAggregate(None, None)
    }

    listSalary
  }

  def getValidDates(dates: Seq[PaymentInfo]): Seq[PaymentInfo] = {
    dates
      .sortWith((a, b) => a.date < b.date)
      .takeRight(2)
  }

  def getCountFromList(countsAggregate: CountsAggregate): Int = {
    countsAggregate
      .dates
      .map(a => a.length)
      .getOrElse(0)
  }

  def getCountFromMap(key: String, mapAccounts: Option[Map[String, Seq[PaymentInfo]]]): Int = {
    mapAccounts
      .map(a => a.getOrElse(key, Seq.empty))
      .map(_.length)
      .getOrElse(0)
  }

  def deleteStateAccount(typeEven: CountsType.Count, key: String, salaryCountAggregateState: MapState[String, CountsAggregate]): Unit = {
    val listSalary = if (salaryCountAggregateState.contains(typeEven.toString)) {
      salaryCountAggregateState.get(typeEven.toString)
    } else {
      CountsAggregate(None, None)
    }

    val newState = if (listSalary.datesWithMap.isEmpty) {
      Map(key -> Seq.empty[PaymentInfo])
    } else {
      listSalary.datesWithMap.get ++ Map(key -> Seq.empty[PaymentInfo])
    }

    salaryCountAggregateState.put(typeEven.toString, CountsAggregate(None, Some(newState)))
  }

  def getSalaryFromMap(key: String, mapAccounts: Option[Map[String, Seq[PaymentInfo]]]): Seq[PaymentInfo] = {
    mapAccounts
      .map(a => a.getOrElse(key, Seq.empty))
      .getOrElse(Seq.empty)
  }

  def filterByDay(date: Long): Boolean = {
    val days = (new Date().getTime - date) / (60 * 60 * 24 * 1000)
    days <= 2
  }

  def getSumOfSalary(validDates: Seq[PaymentInfo]): BigDecimal = {
    val amountAll = validDates
      .map(paymentInfo => paymentInfo.paymentSum.getOrElse(BigDecimal(0)))
      .sum

    amountAll
  }

  def getFirstDaysForLastDays(validDates: Seq[PaymentInfo]): Long = {
    val firstDaysForLastDays = validDates
      .map(_.date)

    Try(firstDaysForLastDays.min).toOption.getOrElse(0L)
  }
}
