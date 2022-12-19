package ru.vtb.uasp.filter.configuration.property

import ru.vtb.uasp.common.utils.config.PropertyUtil.mapProperty
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, PropertyCombiner, ReadConfigErrors}


case class KafkaProducerPropertyMap(topicAlias: Map[String, String]){


}


object KafkaProducerPropertyMap extends PropertyCombiner[KafkaProducerPropertyMap] {

  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, KafkaProducerPropertyMap] =
      for {
        topicAlias <- mapProperty(prf, { (str, appProps, ci) => {
          val k = s"$str.topicName"
          ci.readKey.add(k)
          appProps.prop(k)
        } })(appProps, configurationInitialise)
      } yield KafkaProducerPropertyMap(topicAlias)
}
