package ru.vtb.bevent.first.salary.aggregate.factory

import ru.vtb.bevent.first.salary.aggregate.factory
import ru.vtb.uasp.mutator.service.BusinessRulesService

object BusinessRulesFactory {
  def getBusinessRules(listOfBusinessRuleLevel0: List[String],
                       listOfBusinessRuleLevel1: List[String],
                       listOfBusinessRuleLevel2: List[String],
                       listOfBusinessRule: List[String]): BusinessRules = {
    factory.BusinessRules(
      BusinessRulesService(listOfBusinessRuleLevel0),
      BusinessRulesService(listOfBusinessRuleLevel1),
      BusinessRulesService(listOfBusinessRuleLevel2),
      BusinessRulesService(listOfBusinessRule)
    )
  }
}
