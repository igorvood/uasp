package ru.vtb.bevent.first.salary.aggregate.factory

import ru.vtb.uasp.mutator.service.BusinessRulesService

case class BusinessRules(level0: BusinessRulesService, level1: BusinessRulesService, level2: BusinessRulesService, cases: BusinessRulesService)
