package ru.vtb.uasp.mdm.enrichment.utils.config.enrich

import ru.vtb.uasp.common.kafka.{FlinkConsumerProperties, FlinkSinkProperties}
import ru.vtb.uasp.common.utils.config.PropertyUtil.{createByClass, createByClassOption}
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, PropertyCombiner, ReadConfigErrors}
import ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf.{EnrichProperty, EnrichPropertyOut, EnrichPropertyWithDlq}

case class MainEnrichProperty(fromTopic: FlinkConsumerProperties,
                              toTopicProp: FlinkSinkProperties,
                              dlqTopicProp: Option[FlinkSinkProperties],
                             ) extends EnrichProperty with EnrichPropertyWithDlq with EnrichPropertyOut {

}

object MainEnrichProperty extends PropertyCombiner[MainEnrichProperty] {


  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, MainEnrichProperty] =
    for {
      fromTopic <- FlinkConsumerProperties.create(prf)
      toTopicProp <- createByClass(s"$prf.out", FlinkSinkProperties.getClass, { p =>
        FlinkSinkProperties.create(p)
      })
      dlqTopicProp <- createByClassOption(s"$prf.dlq", FlinkSinkProperties.getClass, { p =>
        FlinkSinkProperties.create(p)
      })


    } yield MainEnrichProperty(fromTopic, toTopicProp, dlqTopicProp)
}

