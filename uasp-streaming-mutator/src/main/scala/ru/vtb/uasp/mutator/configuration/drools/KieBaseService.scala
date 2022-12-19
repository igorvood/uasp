package ru.vtb.uasp.mutator.configuration.drools

import org.kie.api.KieBase
import org.kie.api.io.ResourceType
import org.kie.internal.builder.{KnowledgeBuilder, KnowledgeBuilderFactory}
import org.kie.internal.io.ResourceFactory

import scala.collection.JavaConverters.collectionAsScalaIterableConverter

class KieBaseService(kbPaths: List[String]) extends Serializable {
  require(kbPaths.nonEmpty, "List drl file must be not empty")
  val kBase: KieBase = createKBase

  private def createKBase: KieBase = {
    val config = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration()
    config.setProperty("drools.dialect.mvel.strict", "false")
    val kBuilder: KnowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(config)

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

    val base = kBuilder.newKieBase()

    base
  }
}
