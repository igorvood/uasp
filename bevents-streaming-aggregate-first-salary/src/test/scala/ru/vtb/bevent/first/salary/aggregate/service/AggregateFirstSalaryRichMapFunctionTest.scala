package ru.vtb.bevent.first.salary.aggregate.service

import org.apache.flink.api.scala.typeutils.Types
import org.apache.flink.streaming.api.operators.KeyedProcessOperator
import org.apache.flink.streaming.util.KeyedOneInputStreamOperatorTestHarness
import org.scalatest.flatspec.AnyFlatSpec
import ru.vtb.bevent.first.salary.aggregate.UaspStreamingAggregateFirstSalary
import ru.vtb.bevent.first.salary.aggregate.factory.BusinessRulesFactory
import ru.vtb.uasp.common.dto.UaspDto

import java.util.Date
import scala.collection.JavaConverters.iterableAsScalaIterableConverter

class AggregateFirstSalaryRichMapFunctionTest extends AnyFlatSpec {

  behavior of "AggregateFirstSalaryRichMapFunction"

  it should " test enrichMainStream with Optional value but it is empty" in {

    val stringToString = Map(
      "aggregate-first-salary.list.of.business.rule.level0" -> "source_account.drl",
      "aggregate-first-salary.list.of.business.rule.level1" -> "level1.drl",
      "aggregate-first-salary.list.of.business.rule.level2" -> "level2.drl",
      "aggregate-first-salary.list.of.business.rule" -> "case_71.drl,case_8.drl,case_29.drl,case_38.drl,case_39.drl,case_44.drl,case_56.drl,case_57.drl"

    )

    val rules = BusinessRulesFactory.getBusinessRules(
      List("source_account.drl"),
      List("level1.drl"),
      List("level2.drl"),
      List("case_71.drl", "case_8.drl", "case_29.drl", "case_38.drl", "case_39.drl", "case_44.drl", "case_56.drl", "case_57.drl"))

    val aggregateFirstSalaryRichMapFunction = new AggregateFirstSalaryRichMapFunction(rules, "asd")

    val testHarness = new KeyedOneInputStreamOperatorTestHarness[String, UaspDto, UaspDto](new KeyedProcessOperator(aggregateFirstSalaryRichMapFunction), UaspStreamingAggregateFirstSalary.keySlector, Types.STRING)
    testHarness.open()

    val dto = UaspDto(
      id = "",
      dataInt = Map(
        "currency_scale" -> 1,
        "transaction_currency_cd" -> 840,
      ),
      dataLong = Map(
        "transmission_dttm" -> new Date().getTime
      ),
      dataFloat = Map(),
      dataDouble = Map(),
      dataDecimal = Map(
        "transaction_amt" -> 5000,
        "currency_price" -> 76.39780,
      ),
      dataString = Map(
        "message_type" -> "0100",
        "transaction_cd" -> "110",
        "terminal_class" -> "002",
        "hash_card_number" -> "123456789123456789"
      ),
      dataBoolean = Map(
      ),
      uuid = "",
      process_timestamp = 0
    )

    (0 to 10)
      .foreach(i => testHarness.processElement(dto, i))

    val list = testHarness.extractOutputStreamRecords().asScala.map(q => q.getValue).toList
    assertResult(11)(list.size)


    assertResult(1)(list.count(d => d.dataInt.getOrElse("COUNT_POS", -1) == 0))
    assertResult(1)(list.count(d => d.dataInt.getOrElse("COUNT_POS_CARD", -1) == 0))

    assertResult(1)(list.count(d => d.dataInt.getOrElse("COUNT_POS", -1) == 1))
    assertResult(1)(list.count(d => d.dataInt.getOrElse("COUNT_POS_CARD", -1) == 1))

    assertResult(1)(list.count(d => d.dataInt.getOrElse("COUNT_POS", -1) == 2))
    assertResult(1)(list.count(d => d.dataInt.getOrElse("COUNT_POS_CARD", -1) == 2))


    assertResult(8)(list.count(d => d.dataInt.getOrElse("COUNT_POS", -1) == 3))
    assertResult(8)(list.count(d => d.dataInt.getOrElse("COUNT_POS_CARD", -1) == 3))


  }


}
