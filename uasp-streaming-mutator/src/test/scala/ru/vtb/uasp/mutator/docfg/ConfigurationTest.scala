package ru.vtb.uasp.mutator.docfg

import org.scalatest.flatspec.AnyFlatSpec
import ru.vtb.uasp.common.generate.ConfigurationChecker
import ru.vtb.uasp.common.generate.dto.Profile
import ru.vtb.uasp.mutator.DroolsBusinessRullesJob
import ru.vtb.uasp.mutator.configuration.property.MutationConfiguration
import ru.vtb.uasp.mutator.configuration.property.MutationConfiguration.appPrefixDefaultName

class ConfigurationTest extends AnyFlatSpec {

  private implicit val profiles: List[Profile] = List(
    Profile("mainInput"),
    Profile("uddsRate"),
    Profile("rateMutate"),
    Profile("case-48-concatenate"),
  )

  behavior of "Create and check Configuration"

  it should " check configuration is ok" in {
    ConfigurationChecker.createConfiguration(
      filePrefix = "generated-mutator",
      profiles = profiles,
      clazz = DroolsBusinessRullesJob.getClass,
      combiner = MutationConfiguration,
      propertyPrefix = appPrefixDefaultName)
  }

}
