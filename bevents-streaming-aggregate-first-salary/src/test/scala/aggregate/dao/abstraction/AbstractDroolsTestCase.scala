package aggregate.dao.abstraction

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.common.extension.CommonExtension.Also
import ru.vtb.uasp.mutator.configuration.drools.KieBaseService
import ru.vtb.uasp.mutator.service.BusinessRulesService
import ru.vtb.uasp.mutator.service.drools.DroolsRunner
import ru.vtb.uasp.mutator.service.dto.UaspOperation

import scala.annotation.tailrec
import scala.util.Failure

abstract class AbstractDroolsTestCase extends AnyFlatSpec with should.Matchers {

  protected def drlFileList: List[String]

  protected val businessRulesService: BusinessRulesService = BusinessRulesService(drlFileList)


  protected val validator: DroolsRunner = new KieBaseService(drlFileList)
    .also { q => DroolsRunner(q.kBase) }

  def runDrools(uaspDto: UaspDto, testCase: TestCaseData): Set[UaspOperation] = {
    runDrools(uaspDto, List(testCase))
  }

  def runDrools(uaspDto: UaspDto, testCase: List[TestCaseData]): Set[UaspOperation] = {
    val value = modifyListTestData(uaspDto, testCase)
    validator.apply(value, { case x: UaspOperation => x })
  }

  protected def assertTry[T](triedAssertions: List[(T, Object)]): Unit = {
    println(s"total testcases ${triedAssertions.size}")

    val triedBooleans = triedAssertions
      .filter(_._2.isInstanceOf[Failure[Boolean]])
      .map(d => s"error: ${d._2.asInstanceOf[Failure[Boolean]].exception.getMessage}\nfor testcase ${d._1}")
      .mkString("\n-----------------------------------------------\n")
    assertResult("")(triedBooleans)
  }

  @tailrec
  final protected def modifyListTestData(uaspDtoEtalon: UaspDto, testCase: List[TestCaseData]): UaspDto = {
    testCase match {
      case Nil => uaspDtoEtalon
      case x :: Nil => modifyTestData(uaspDtoEtalon, x)
      case x :: xs => modifyListTestData(modifyTestData(uaspDtoEtalon, x), xs)
    }
  }

  protected def modifyTestData(uaspDtoEtalon: UaspDto, testCase: TestCaseData): UaspDto = {
    testCase.action match {
      case NoneTestAction() => uaspDtoEtalon
      case AddTestAction(v) => modify(uaspDtoEtalon, testCase, v)
      case MutateTestAction(v) => modify(uaspDtoEtalon, testCase, v)
      case DeleteTestAction() => delete(uaspDtoEtalon, testCase)
    }
  }

  private def modify(uaspDto: UaspDto, testCase: TestCaseData, v: String): UaspDto = {
    val dto = testCase match {
      case TestInt(_, _, _) => uaspDto.copy(dataInt = uaspDto.dataInt ++ Map(testCase.nameField -> v.toInt))
      case TestLong(_, _, _) => uaspDto.copy(dataLong = uaspDto.dataLong ++ Map(testCase.nameField -> v.toLong))
      case TestFloat(_, _, _) => uaspDto.copy(dataFloat = uaspDto.dataFloat ++ Map(testCase.nameField -> v.toFloat))
      case TestDouble(_, _, _) => uaspDto.copy(dataDouble = uaspDto.dataDouble ++ Map(testCase.nameField -> v.toDouble))
      case TestBigDecimal(_, _, _) => uaspDto.copy(dataDecimal = uaspDto.dataDecimal ++ Map(testCase.nameField -> BigDecimal(v)))
      case TestString(_, _, _) => uaspDto.copy(dataString = uaspDto.dataString ++ Map(testCase.nameField -> v))
      case TestBoolean(_, _, _) => uaspDto.copy(dataBoolean = uaspDto.dataBoolean ++ Map(testCase.nameField -> v.toBoolean))
    }
    dto
  }

  private def delete(uaspDto: UaspDto, testCase: TestCaseData): UaspDto = {
    val dto = testCase match {
      case TestInt(_, _, _) => uaspDto.copy(dataInt = uaspDto.dataInt - testCase.nameField)
      case TestLong(_, _, _) => uaspDto.copy(dataLong = uaspDto.dataLong - testCase.nameField)
      case TestFloat(_, _, _) => uaspDto.copy(dataFloat = uaspDto.dataFloat - testCase.nameField)
      case TestDouble(_, _, _) => uaspDto.copy(dataDouble = uaspDto.dataDouble - testCase.nameField)
      case TestBigDecimal(_, _, _) => uaspDto.copy(dataDecimal = uaspDto.dataDecimal - testCase.nameField)
      case TestString(_, _, _) => uaspDto.copy(dataString = uaspDto.dataString - testCase.nameField)
      case TestBoolean(_, _, _) => uaspDto.copy(dataBoolean = uaspDto.dataBoolean - testCase.nameField)
    }
    dto
  }
}
