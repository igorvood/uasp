package ru.vtb.uasp.mdm.enrichment.utils.config

import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.utils.config.PropertyUtil._
import ru.vtb.uasp.common.utils.config.kafka.KafkaPrdProperty
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, PropertyCombiner, ReadConfigErrors}
import ru.vtb.uasp.mdm.enrichment.service._
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.AllEnrichProperty

import scala.collection.mutable

case class MDMEnrichmentPropsModel(
                                    appServiceName: String,
                                    appSavepointPref: String,
                                    allEnrichProperty: AllEnrichProperty,
                                    appSyncParallelism: Int,
                                  ) {

  require(appSyncParallelism > 0, "appSyncParallelism must be grater than zero")

  lazy val throwToDlqService = new ThrowToDlqService

  //  Вытаскивание ключевого значсения для основного потока, для обогащения глобальным идентификатором
  lazy val globalMainStreamExtractKeyFunction = allEnrichProperty.globalIdEnrichProperty
    .map { g => new MainStreamExtractKeyFunction(g) }

  lazy val commonMainStreamExtractKeyFunction = allEnrichProperty.commonEnrichProperty
    .map { c => new MainStreamExtractKeyFunction(c) }

  //консьюмеры

  lazy val mainInputStream: FlinkKafkaConsumer[Array[Byte]] = allEnrichProperty.mainEnrichProperty.fromTopic.createConsumer()

  lazy val commonConsumer: Option[FlinkKafkaConsumer[Array[Byte]]] = allEnrichProperty.commonEnrichProperty
    .map { prp =>
      prp.fromTopic.createConsumer()
    }

  lazy val globalIdConsumer: Option[FlinkKafkaConsumer[Array[Byte]]] = allEnrichProperty.globalIdEnrichProperty
    .map { prp =>
      prp.fromTopic.createConsumer()
    }

  // common services
  lazy val keyCommonEnrichmentMapService: Option[KeyedEnrichCommonCoProcessService] = allEnrichProperty.commonEnrichProperty
    .map(cp =>
      new KeyedEnrichCommonCoProcessService(cp)
    )

  lazy val commonValidateProcessFunction: Option[ExtractKeyFunction] = allEnrichProperty.commonEnrichProperty
    .map(cp => new ExtractKeyFunction(cp))

  // global id services
  lazy val keyGlobalIdEnrichmentMapService: Option[KeyGlobalIdEnrichmentMapService] =
    allEnrichProperty.globalIdEnrichProperty
      .map(glbProp => new KeyGlobalIdEnrichmentMapService(
        glbProp,
        appSavepointPref))

  lazy val validateGlobalIdService: Option[ExtractKeyFunction] = allEnrichProperty.globalIdEnrichProperty
    .map(glbProp => new ExtractKeyFunction(glbProp))


  lazy val flinkSinkPropertiesMainProducer: FlinkSinkProperties = allEnrichProperty.mainEnrichProperty.toTopicProp

  lazy val flinkSinkPropertiesGlobalIdProducerDLQ: Option[FlinkSinkProperties] = allEnrichProperty.globalIdEnrichProperty
    .flatMap(alias => alias.dlqTopicProp)

  lazy val flinkSinkPropertiesCommonProducerDLQ: Option[FlinkSinkProperties] = allEnrichProperty.commonEnrichProperty
    .flatMap(alias => alias.dlqTopicProp)

}


object MDMEnrichmentPropsModel extends PropertyCombiner[MDMEnrichmentPropsModel] with ConfigurationInitialise[MDMEnrichmentPropsModel] {
  val appPrefixDefaultName: String = "uasp-streaming-mdm-enrichment"

  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, MDMEnrichmentPropsModel] =
    for {
      appServiceName <- propertyVal[String](s"$prf", "service.name")(appProps, configurationInitialise, s)
      appSavepointPref <- propertyVal[String](s"$prf", "savepoint.pref")(appProps, configurationInitialise, s)
      allEnrichProperty <- AllEnrichProperty.create(s"$prf.enrichOne")
      appSyncParallelism <- propertyVal[Int](s"$prf", "sync.parallelism")
    } yield MDMEnrichmentPropsModel(appServiceName, appSavepointPref, allEnrichProperty, appSyncParallelism)


  override def defaultConfiguration(prf: String)(implicit allProps: AllApplicationProperties, readKey: mutable.Set[String]): MDMEnrichmentPropsModel = MDMEnrichmentPropsModel(prf)(allProps, MDMEnrichmentPropsModel)
}