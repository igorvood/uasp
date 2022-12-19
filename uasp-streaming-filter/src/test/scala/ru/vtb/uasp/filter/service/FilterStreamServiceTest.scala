package ru.vtb.uasp.filter.service

import org.apache.flink.streaming.util.{OneInputStreamOperatorTestHarness, ProcessFunctionTestHarnesses}
import org.scalatest.BeforeAndAfter
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.filter.configuration.property.FilterRule
import ru.vtb.uasp.filter.service.FilterStreamServiceTestConfiguration.{dtoNoFields, expectedMap, filterRulesMap, uaspDtoWithFilelds}
import ru.vtb.uasp.filter.service.dto._

class FilterStreamServiceTest extends org.scalatest.flatspec.AnyFlatSpec with should.Matchers with BeforeAndAfter {

  "test all rules for Boolean" should " be success " in {
    testType({ r => r._2.operandClass.isInstanceOf[BooleanOperand] })
  }

  "test all rules for LongOperand" should " be success " in {
    testType({ r => r._2.operandClass.isInstanceOf[LongOperand] })
  }

  "test all rules for IntOperand" should " be success " in {
    testType({ r => r._2.operandClass.isInstanceOf[IntOperand] })
  }

  "test all rules for FloatOperand" should " be success " in {
    testType({ r => r._2.operandClass.isInstanceOf[FloatOperand] })
  }

  "test all rules for DoubleOperand" should " be success " in {
    testType({ r => r._2.operandClass.isInstanceOf[DoubleOperand] })
  }

  "test all rules for BigDecimalOperand" should " be success " in {
    testType({ r => r._2.operandClass.isInstanceOf[BigDecimalOperand] })
  }

  "test all rules for StringOperand" should " be success " in {
    testType({ r => r._2.operandClass.isInstanceOf[StringOperand] })
  }


  "test all rules for all operand type" should " throw exception " in {
    filterRulesMap
      .foreach { rule =>
        val filterProcessFunction = new FilterProcessFunction(rule._2)
        val harness: OneInputStreamOperatorTestHarness[UaspDto, UaspDto] = ProcessFunctionTestHarnesses.forProcessFunction(filterProcessFunction)

        harness.processElement(dtoNoFields, 1)
        val ok = harness.extractOutputStreamRecords()
        val err = harness.getSideOutput(filterProcessFunction.dlqOutPut)

        rule._2.operatorClass match {
          case NotNull() => assert(ok.size() == 0 & err.size() == 1)
          case Null() => assert(ok.size() == 1 && err == null)
          case _ => assert(ok.size() == 0 && err.size() == 1)
        }
      }
  }

  private def testType(function: ((String, FilterRule)) => Boolean): Unit = {
    filterRulesMap
      .filter(function)
      .foreach { rule =>
        val filterProcessFunction = new FilterProcessFunction(rule._2)
        val harness: OneInputStreamOperatorTestHarness[UaspDto, UaspDto] = ProcessFunctionTestHarnesses.forProcessFunction(filterProcessFunction)
        uaspDtoWithFilelds.foreach { uaspDto =>
          harness.processElement(uaspDto, 1)
        }
        val expectedCount = expectedMap(rule._1)

        val queue = harness.extractOutputStreamRecords()
        val actualOk = Option(queue)
          .map(q => q.size())
          .getOrElse(0)

        val actualErr = Option(harness.getSideOutput(filterProcessFunction.dlqOutPut))
          .map(q => q.size())
          .getOrElse(0)
        val actualCount = FilteredCount(actualOk, actualErr)


        assertResult(expectedCount, s"for type ${rule._2.operandClass} and operation  ${rule._2.operatorClass} count not equal expected")(actualCount)

      }
  }
}
