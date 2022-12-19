package ru.vtb.uasp.mutator.configuration

import ru.vtb.uasp.common.utils.config.AllApplicationProperties
import ru.vtb.uasp.common.utils.config.ConfigUtils.readAllPropsByProfile

object MutationConfigurationUtil {
  def initAllProps(args: Array[String]): AllApplicationProperties = {
    if (args.isEmpty) throw new IllegalArgumentException("args is empty")
    readAllPropsByProfile(args)
  }

}
