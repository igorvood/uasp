package ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf

import ru.vtb.uasp.common.kafka.FlinkSinkProperties

trait EnrichPropertyOut {

  /*топик куда записываются успешно обработанные сообщения*/
  val toTopicProp: FlinkSinkProperties

  require(toTopicProp != null, "toTopicName must be not null")
}
