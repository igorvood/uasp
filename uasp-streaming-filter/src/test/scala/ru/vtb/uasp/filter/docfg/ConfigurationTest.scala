package ru.vtb.uasp.filter.docfg

import org.scalatest.flatspec.AnyFlatSpec
import ru.vtb.uasp.common.generate.ConfigurationChecker
import ru.vtb.uasp.common.generate.dto.Profile
import ru.vtb.uasp.filter.FilterJob
import ru.vtb.uasp.filter.configuration.property.FilterConfiguration
import ru.vtb.uasp.filter.configuration.property.FilterConfiguration.appPrefixDefaultName

class ConfigurationTest extends AnyFlatSpec {

  private implicit val profiles: List[Profile] = List(
    Profile("main-input-filter"),
    Profile("bevents-filter"),
  )

  behavior of "Create and check Configuration"

  ignore should " check configuration is ok" in {
    ConfigurationChecker.createConfiguration(
      filePrefix = "generated-filter",
      profiles = profiles,
      clazz = FilterJob.getClass,
      combiner = FilterConfiguration,
      propertyPrefix = appPrefixDefaultName)
  }

}
