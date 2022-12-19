package ru.vtb.uasp.common.utils.config
import scala.collection.mutable

case class SomeClassName()

object SomeClassName extends ConfigurationInitialise[SomeClassName]{
  override def defaultConfiguration(prf: String)(implicit allProps: AllApplicationProperties, readKey: mutable.Set[String]): SomeClassName =
    SomeClassName(prf)(allProps, SomeClassName)

  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, SomeClassName] =
    Right(SomeClassName())
}