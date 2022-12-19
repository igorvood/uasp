package ru.vtb.uasp.common.utils.config

import ru.vtb.uasp.common.utils.config.PrefixProperty.PredefPrefix

import scala.util.{Failure, Success, Try}

trait PropertyCombiner[T] {


  def create[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, T] ={
    Try {
      createMayBeErr[CONFIGURATION](prf)
    } match {
      case Success(value) => value
      case Failure(exception) => Left(ReadConfigErrors(List(s"Unable to create property by prefix '$prf', reason: ${exception.getMessage}")))
    }
  }

  protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, T]

  def apply[CONFIGURATION](prefix: String)(implicit appProps: AllApplicationProperties,  configurationInitialise: ConfigurationInitialise[CONFIGURATION]): T =
    prefix createProperty { prf =>
      val errorsOrT = prf createProperty { p => createMayBeErr(p)(appProps, configurationInitialise) }
      val t = errorsOrT match {
        case Right(value) => value
        case Left(value) => throw new IllegalStateException(s"Unable to create property by prefix '$prf', reason: ${value.errors.mkString("\n")}")
      }
      t
    }

}