package ru.vtb.uasp.mdm.enrichment.utils.config

import ru.vtb.uasp.common.kafka.FlinkSinkProperties
import ru.vtb.uasp.common.service.dto.ServiceDataDto
import ru.vtb.uasp.common.utils.config.PropertyUtil._
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, PropertyCombiner, ReadConfigErrors}
import ru.vtb.uasp.mdm.enrichment.service._
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.AllEnrichProperty

import scala.collection.mutable

case class MDMEnrichmentPropsModel(
                                    serviceData: ServiceDataDto,
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

}


object MDMEnrichmentPropsModel extends PropertyCombiner[MDMEnrichmentPropsModel] with ConfigurationInitialise[MDMEnrichmentPropsModel] {
  val appPrefixDefaultName: String = "uasp-streaming-mdm-enrichment"

  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, MDMEnrichmentPropsModel] =
    for {
      appServiceName <- ServiceDataDto.create(s"$prf.service")
      appSavepointPref <- propertyVal[String](s"$prf", "savepoint.pref")(appProps, configurationInitialise, s)
      allEnrichProperty <- AllEnrichProperty.create(s"$prf.enrichOne")
      appSyncParallelism <- propertyVal[Int](s"$prf", "sync.parallelism")
    } yield MDMEnrichmentPropsModel(appServiceName, appSavepointPref, allEnrichProperty, appSyncParallelism)


  override def defaultConfiguration(prf: String)(implicit allProps: AllApplicationProperties, readKey: mutable.Set[String]): MDMEnrichmentPropsModel = MDMEnrichmentPropsModel(prf)(allProps, MDMEnrichmentPropsModel)
}