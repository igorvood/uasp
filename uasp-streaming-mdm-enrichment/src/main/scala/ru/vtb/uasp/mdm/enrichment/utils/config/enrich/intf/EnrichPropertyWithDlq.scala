package ru.vtb.uasp.mdm.enrichment.utils.config.enrich.intf

import ru.vtb.uasp.common.kafka.FlinkSinkProperties

trait EnrichPropertyWithDlq {
  /*топик с обратным потоком, содержащий сообщение Way4 уже обогащенное глобальным идентификатором*/
  val dlqTopicProp: Option[FlinkSinkProperties]

  require(dlqTopicProp != null, "dlqTopicName must be not null")

}
