package ru.vtb.uasp.filter.configuration.property

import ru.vtb.uasp.common.utils.config.PropertyUtil._
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, PropertyCombiner, ReadConfigErrors}

case class ExecutionFlinkEnvironmentProperty(
                                              appServiceName: String,
                                              syncParallelism: Int,
                                            ) {
  require(syncParallelism > 1, "syncParallelism must gather then ")
}

object ExecutionFlinkEnvironmentProperty extends PropertyCombiner[ExecutionFlinkEnvironmentProperty] {


  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, ExecutionFlinkEnvironmentProperty] =
    for {appServiceName <- propertyVal[String](prf, "service.name")(appProps, configurationInitialise, s)
         syncParallelism <- propertyVal[Int](prf, "sync.parallelism")
         } yield ExecutionFlinkEnvironmentProperty(appServiceName, syncParallelism)
}
