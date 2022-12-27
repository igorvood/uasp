package ru.vtb.uasp.common.utils.config

import ru.vtb.uasp.common.utils.config.PropertyUtil.extractNamesPlaceholder

import scala.collection.mutable

trait ConfigurationInitialise[CONFIGURATION] extends PropertyCombiner[CONFIGURATION] {

  implicit val readKey: mutable.Set[String] = mutable.Set()

  implicit val resolvablePlaceHolders: mutable.Map[String, String] = mutable.Map()

  def configApp(prf: String, args: Array[String]): CONFIGURATION = {
    implicit val allApplicationProperties: AllApplicationProperties = ConfigUtils.readAllPropsByProfile(args)

    val prop = allApplicationProperties.prop
    prop
      .filter { p => p._2.contains("${") && p._2.contains("}") }
      .flatMap(q => extractNamesPlaceholder(q._2))
      .flatMap(ph => prop.get(ph).map(v => ph -> v))
      .foreach(ph => resolvablePlaceHolders.put(ph._1, ph._2))

    val configuration = defaultConfiguration(prf)

    checkNotUsed(this, allApplicationProperties)
    configuration
  }

  def defaultConfiguration(prf: String)(implicit allProps: AllApplicationProperties, readKey: mutable.Set[String]): CONFIGURATION

  private def checkNotUsed[T](combiner: ConfigurationInitialise[T], properties: AllApplicationProperties): Unit = {
    val keysNotRead = properties.prop.keys.filter(key =>
      !combiner.readKey.contains(key)
    ).toList

    if (keysNotRead.nonEmpty) {
      val value = keysNotRead.mkString("\n")
      val reads = combiner.readKey.mkString("\n")
      throw new IllegalStateException(s"Not used ${keysNotRead.size} key for \n$value\n READS VALUE:\n$reads")
    }
  }

}

object ConfigurationInitialise {
  implicit val argsToPropFun: Array[String] => AllApplicationProperties = { arg =>
    ConfigUtils.readAllPropsByProfile(arg)
  }
}
