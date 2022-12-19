package ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import ru.vtb.uasp.common.dto.UaspDto
import ru.vtb.uasp.mdm.enrichment.service.dto.KeyedUasp
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf.EnrichPropertyFieldsTest._
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.{EnrichFields, KeySelectorProp}

import scala.util.{Failure, Try}

class EnrichPropertyFieldsTest extends AnyFlatSpec with Matchers {


  it should "прогон всех тесто на выявление ключевого поля" in {

    val triedAssertions = testCases
      .map { tc =>
        tc -> Try {
          implicit val maybeErr: Either[String, KeyedUasp] = EnrichPropertyFieldsTestDto(tc.keySelectorMain, tc.keySelectorEnrich)
            .calcKey(tc.uaspDto)

          assertDto(tc.expected.localIdNew, tc.expected.errText)
        }
      }

    val errorsList = triedAssertions
      .collect { case (TestCase(keySelectorMain, keySelectorEnrich, enrichmentUaspWithError, expected), Failure(exception)) =>
        exception.getMessage + " for test " + TestCase(keySelectorMain, keySelectorEnrich, enrichmentUaspWithError, expected)
      }

    val errs = errorsList
      .mkString("\n")

    if (errs.nonEmpty) {
      val value1 = s"from ${triedAssertions.size} test cases ${errorsList.size} is failed \n"
      assert(false, value1 + errs)
    }

    println(triedAssertions.size + " успешных тестов")
  }


  private def assertDto(localIdNew: String, errText: String)(implicit maybeErr: Either[String, KeyedUasp]) = {
    if (errText.isEmpty)
      maybeErr
        .map { keyed =>
          assertResult(localIdNew)(keyed.localId)
        }
        .getOrElse(throw new IllegalArgumentException(s"must be Rigth, current value is $maybeErr"))
    else {
      val expecteErr = maybeErr.left.get
      assertResult(errText)(expecteErr)
    }
  }
}

object EnrichPropertyFieldsTest {

  protected val mainAlias = "mainAlias"
  protected val secondaryAlias = "secondaryAlias"
  protected val otherSecondaryAlias = "otherSecondaryAlias"

  protected def testCases: List[TestCase] = List(

    TestCase(
      KeySelectorProp(true, None, None),
      KeySelectorProp(true, None, None),
      inEmptyUaspDto,
      Expected(undefined, "Uasp dto 'id' is null")),

    TestCase(
      KeySelectorProp(true, None, None),
      KeySelectorProp(true, None, None),
      inEmptyUaspDto.copy(id = "currentId"),
      Expected("currentId")),


    TestCase(
      KeySelectorProp(false, Some("String"), Some("String")),
      KeySelectorProp(true, None, None),
      inEmptyUaspDto,
      Expected(undefined, "Unable to find value in map String for group by key String")),

    TestCase(
      KeySelectorProp(false, Some("String"), Some("String")),
      KeySelectorProp(true, None, None),
      inEmptyUaspDto.copy(dataString = Map("String" -> "someValue")),
      Expected("someValue")),
  )

  protected def undefined = "undefined"

  val inEmptyUaspDto = UaspDto(null, Map(), Map(), Map(), Map(), Map(), Map(), Map(), null, 0)


  case class EnrichPropertyFieldsTestDto(
                                          keySelectorMain: KeySelectorProp,
                                          keySelectorEnrich: KeySelectorProp,
                                          fields: List[EnrichFields] = List()
                                        ) extends EnrichPropertyFields

  case class TestCase(keySelectorMain: KeySelectorProp,
                      keySelectorEnrich: KeySelectorProp,
                      uaspDto: UaspDto,
                      expected: Expected
                     )

  case class Expected(localIdNew: String, errText: String = "")
}