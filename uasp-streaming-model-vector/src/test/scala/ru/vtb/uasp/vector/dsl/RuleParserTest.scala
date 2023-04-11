package ru.vtb.uasp.vector.dsl

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper


class RuleParserTest extends AnyFlatSpec {
  "RuleParserTest.caseRulesMap " should "be equals" in {
    RuleParser.caseRulesMap.size shouldBe 9
  }

}
