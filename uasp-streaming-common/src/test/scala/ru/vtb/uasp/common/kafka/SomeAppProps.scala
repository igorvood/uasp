package ru.vtb.uasp.common.kafka

import ru.vtb.uasp.common.utils.config.PropertyUtil.createByClass
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, ReadConfigErrors}

import scala.collection.mutable

case class SomeAppProps(
                         flinkSinkProperties: FlinkSinkProperties
                       )

object SomeAppProps extends ConfigurationInitialise[SomeAppProps] {
  override def defaultConfiguration(prf: String)(implicit allProps: AllApplicationProperties, readKey: mutable.Set[String]): SomeAppProps =
    SomeAppProps(prf)(allProps, SomeAppProps)

  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, SomeAppProps] = {
    for {
      flinkSinkProperties <- createByClass(prf, FlinkSinkProperties.getClass, { p =>
        FlinkSinkProperties.create(p)
      })
    } yield SomeAppProps(flinkSinkProperties)

  }
}
