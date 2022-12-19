package ru.vtb.uasp.mutator.service.drools

trait DroolsRunnerService {
  def apply[IN_TYPE, DRL_TYPE](model: IN_TYPE, pf: PartialFunction[Any, DRL_TYPE]): Set[DRL_TYPE]
}
