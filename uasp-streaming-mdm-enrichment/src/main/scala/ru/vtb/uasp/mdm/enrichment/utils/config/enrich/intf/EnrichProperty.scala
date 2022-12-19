package ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf

import ru.vtb.uasp.common.kafka.FlinkConsumerProperties

trait EnrichProperty {
  /*топик откуда вычитывается информация*/
  val fromTopic: FlinkConsumerProperties

}
