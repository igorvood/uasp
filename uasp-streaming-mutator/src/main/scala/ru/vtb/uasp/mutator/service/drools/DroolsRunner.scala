package ru.vtb.uasp.mutator.service.drools


import org.kie.api.KieBase

import scala.collection.JavaConverters.collectionAsScalaIterableConverter

case class DroolsRunner(kieBase: KieBase) extends DroolsRunnerService with Serializable {

  override def apply[IN_TYPE, DRL_TYPE](model: IN_TYPE, pf: PartialFunction[Any, DRL_TYPE]): Set[DRL_TYPE] = {
    val value1 = using(kieBase.newKieSession()) { session =>
      session.insert(model)
      session.fireAllRules()
      session.getObjects()
    }
    value1.asScala.collect(pf).toSet
  }

  private def using[R, T](getRes: => T)(doIt: T => R)(implicit ev$1: T => ({def dispose(): Unit})): R = {
    val res = getRes
    try doIt(res) finally res.dispose()
  }
}


