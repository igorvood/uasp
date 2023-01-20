package ru.vtb.uasp.common.service.dto


import ru.vtb.uasp.common.utils.config.PropertyUtil.{propertyVal, s}
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, PropertyCombiner, ReadConfigErrors}

case class ServiceDataDto(serviceName: String,
                          serviceProfile: String,
                          serviceVersion: String,
                         ){
  require(serviceName!=null && serviceName.nonEmpty, {"serviceName is empty"})
  require(serviceProfile!=null && serviceProfile.nonEmpty, {"serviceProfile is empty"})
  require(serviceVersion!=null && serviceVersion.nonEmpty, {"serviceVersion is empty"})

  lazy val serviceNameNoVersion: String = s"${serviceName}_$serviceProfile"
  lazy val fullServiceName: String = s"${serviceName}_${serviceProfile}_$serviceVersion"
}

object ServiceDataDto extends PropertyCombiner[ServiceDataDto]{
  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, ServiceDataDto] =
    for {
      serviceName <- propertyVal[String](prf, "serviceName")(appProps, configurationInitialise, s)
      serviceProfile <- propertyVal[String](prf, "serviceProfile")(appProps, configurationInitialise, s)
      serviceVersion <- propertyVal[String](prf, "serviceVersion")(appProps, configurationInitialise, s)
    } yield new ServiceDataDto(
      serviceName, serviceProfile, serviceVersion
    )
}