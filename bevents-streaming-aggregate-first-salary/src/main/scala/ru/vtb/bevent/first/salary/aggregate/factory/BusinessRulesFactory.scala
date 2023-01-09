package ru.vtb.bevent.first.salary.aggregate.factory

import ru.vtb.bevent.first.salary.aggregate.constants.ConfirmedPropsModel
import ru.vtb.bevent.first.salary.aggregate.factory
import ru.vtb.uasp.mutator.service.BusinessRulesService

object BusinessRulesFactory {
  def getBusinessRules(props: ConfirmedPropsModel): BusinessRules = {
    factory.BusinessRules(
        BusinessRulesService(props.listOfBusinessRuleLevel0),
        BusinessRulesService(props.listOfBusinessRuleLevel1),
        BusinessRulesService(props.listOfBusinessRuleLevel2),
        BusinessRulesService(props.listOfBusinessRule)
    )
  }
}
