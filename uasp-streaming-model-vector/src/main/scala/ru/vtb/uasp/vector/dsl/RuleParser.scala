package ru.vtb.uasp.vector.dsl

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.streaming.api.scala.OutputTag
import play.api.libs.json.Json
import ru.vtb.uasp.common.service.dto.KafkaDto
import ru.vtb.uasp.vector.dsl.dto.CaseRules
import ru.vtb.uasp.vector.util.FileUtils

import java.util.Properties
import scala.util.Random

object RuleParser {
  private val defaultCaseRulesPath = "src/main/resources/cases"
  val caseRulesMap: Map[String, CaseRules] = generateCaseRulesMap
  val tags: Map[String, OutputTag[KafkaDto]] = generateTags(caseRulesMap)

  private def parseRawCaseRules: List[CaseRules] = {
    FileUtils
      .getListFileContentFromPath(defaultCaseRulesPath)
      .map(raw => Json.parse(raw).validate[CaseRules])
      .map(js => js.get)
  }

  private def generateCaseRulesMap: Map[String, CaseRules] = {
    parseRawCaseRules
      .flatMap(rc => Map(rc.caseName -> rc))
      .toMap
  }

  //TODO DELETE
/*  //TODO Separate to normal structure
  def generateSinks(rules: Map[String, CaseRules])(implicit propsModel: CasePropsModel): Map[String, FlinkKafkaProducer[JsObject]] = {
    rules
      .map(rule => {
        val kafkaSerializationSchemaFS = new FlinkKafkaSerializationSchema(propsModel.topicsByCases(rule._1))
        val producer = new FlinkKafkaProducer(
          propsModel.topicsByCases(rule._1),
          kafkaSerializationSchemaFS,
          setTransactionIdInProperties(rule._1, propsModel.commonKafkaProps),
          Semantic.EXACTLY_ONCE,
          propsModel.kafkaProducerPoolSize)

        (rule._1, producer)
      })
  }*/

  def setTransactionIdInProperties(topicName: String, props: Properties): Properties = {
    val localKafkaProps = props.clone.asInstanceOf[Properties]
    localKafkaProps.setProperty("transactional.id", topicName + "-id-" + Random.nextInt(999999999).toString)
    localKafkaProps
  }

  def generateTags(rules: Map[String, CaseRules]): Map[String, OutputTag[KafkaDto]] =
    rules.map(cases => (cases._1, OutputTag[KafkaDto](cases._1)))
}
