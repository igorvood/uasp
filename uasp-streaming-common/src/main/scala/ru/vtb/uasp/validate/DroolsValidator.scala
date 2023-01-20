package ru.vtb.uasp.validate

import org.kie.api.KieBase
import org.kie.api.io.ResourceType
import org.kie.internal.builder.KnowledgeBuilderFactory
import org.kie.internal.io.ResourceFactory
import org.slf4j.LoggerFactory
import ru.vtb.uasp.validate.entity.ValidateMsg

import scala.collection.JavaConverters.collectionAsScalaIterableConverter

class DroolsValidator(kbPaths: List[String]) extends Serializable {
  private val kBase = createKBase
  private val kbDesc = kbPaths.mkString("; ")

  def this(kbPath: String) = this(List(kbPath))

  private def using[R, T](getRes: => T)(doIt: T => R)(implicit ev$1: T => ({def dispose(): Unit})): R = {
    val res = getRes
    try doIt(res) finally res.dispose
  }

  def validate(model: List[Any]): List[ValidateMsg] = {
    using(kBase.newKieSession()) { session =>
      session.setGlobal("logger", LoggerFactory.getLogger(kbDesc))
      model.foreach(session.insert)
      session.fireAllRules()
      session.getObjects()
    }.asScala.collect { case x: ValidateMsg => x }.toList
  }

  private def createKBase: KieBase = {
    val config = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration()
    config.setProperty("drools.dialect.mvel.strict", "false")
    val kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(config)

    kbPaths.foreach { kb =>
      val res = ResourceFactory.newClassPathResource(kb)
      kBuilder.add(res, ResourceType.DRL)
      val errors = kBuilder.getErrors
      if (errors.size() > 0) {
        for (error <- errors.asScala) {
          println(error.getMessage)
        }
        throw new IllegalArgumentException("Problem with the Knowledge base")
      }
    }

    kBuilder.newKieBase()
  }
}
