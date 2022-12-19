package ru.vtb.uasp.common.utils.config

import java.util.Properties
import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}


object PropertyUtil extends Serializable {

  //  implicit val i: String => Int = { s: String => s.toInt }
  implicit val i: String => Int = { s: String => java.lang.Integer.parseInt(s) }
  implicit val l: String => Long = { s: String => java.lang.Long.parseLong(s) }
  implicit val f: String => Float = { s: String => new java.lang.Float(s) }
  implicit val d: String => Double = { s: String => new java.lang.Double(s) }
  implicit val bd: String => BigDecimal = { s => BigDecimal(s) }
  implicit val b: String => Boolean = { s: String => s.toBoolean }
  implicit val s: String => String = { s: String => s }


  private def metaPrefix(prf: String, clazz: Class[_])(implicit appPropImplicit: AllApplicationProperties): Option[String] = {
    val meta = clazz.getSimpleName
    val newPrf = s"$prf$meta."
    val stringToString = appPropImplicit.prop.keys.toList
      .filter(ent => {
        ent.contains(newPrf)
      }

      )
    if (stringToString.isEmpty)
      None
    else {
      val head1 = stringToString.head
      val indexMeta = head1.indexOf(meta)
      val resPrf = head1.substring(0, indexMeta) + meta
      Some(resPrf)
    }
  }

  def createByClass[T](prf: String, clazz: Class[_], init: String => Either[ReadConfigErrors, T])(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[_]): Either[ReadConfigErrors, T] = {
    createByClassOption(prf, clazz, init)
      .map(a => {
        a.getOrElse(throw new IllegalStateException("must contains prefix " + prf + clazz.getSimpleName))
      })
  }

  def createByClassOption[T](prf: String, clazz: Class[_], init: String => Either[ReadConfigErrors, T])(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[_]): Either[ReadConfigErrors, Option[T]] = {
    val str = fullPrefix(prf)

    val stringToString = AllApplicationProperties(appProps.prop.filter(q => q._1.startsWith(str)))

    val prfOption = metaPrefix(str, clazz)(stringToString)
    val value = prfOption match {
      case Some(v) =>
        val errorsOrT = init(v)
        errorsOrT.map(d => Some(d))
      case None => Right(None)
    }
    value
  }

  def propertyVal[T](prefix: String, propName: String)(implicit appPropImplicit: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[_], strToT: String => T): Either[ReadConfigErrors, T] = {
    filterAndMap(fullPrefix(prefix), appPropImplicit.prop)
      .get(propName) match {
      case Some(v) =>
        Try {
          Right(strToT(replaceDifficultPlaceHolders(v)))
        } match {
          case Success(value) =>
            configurationInitialise.readKey.add(s"$prefix.$propName")
            value
          case Failure(exception) => Left(ReadConfigErrors(List(s"Unable to read property '$propName' with prefix '$prefix' error: ${exception.getMessage}")))
        }
      case None => Left(ReadConfigErrors(List(s"Unable to read property '$propName' with prefix '$prefix'. FULL KEY is $prefix.$propName")))
    }
  }

  def propertyValOptional[T](prefix: String, propName: String)(implicit appPropImplicit: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[_], strToT: String => T): Either[ReadConfigErrors, Option[T]] = {
    filterAndMap(fullPrefix(prefix), appPropImplicit.prop)
      .get(propName) match {
      case Some(v) =>
        Try {
          strToT(replaceDifficultPlaceHolders(v))
        } match {
          case Success(value) =>
            configurationInitialise.readKey.add(s"$prefix.$propName")
            Right(Some(value))
          case Failure(exception) => Left(ReadConfigErrors(List(s"Unable to read property '$propName' with prefix '$prefix' error: ${exception.getMessage}")))
        }
      case None => Right(None)
    }
  }


  def asProperty(propPrefix: String)(implicit appPropImplicit: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[_]): Either[ReadConfigErrors, Properties] = {
    val prf = fullPrefix(propPrefix)
    val usedKey = appPropImplicit.prop.keys.filter(key => key.contains(prf))
    usedKey
      .foreach(k => configurationInitialise.readKey.add(k))

    val stringToString = filterAndMap(prf, appPropImplicit.prop)
    Right(getPropsFromMap(stringToString))
  }

  def mapProperty[T](prefix: String,
                     init: (String, AllApplicationProperties, ConfigurationInitialise[_]) => T
                    )(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[_]): Either[ReadConfigErrors, Map[String, T]] = {
    val prf = fullPrefix(prefix)

    val filteredProp = filterByPrefix(prf, appProps.prop)
    val stringToT = filteredProp
      .groupBy({ entry => entry._1.replace(prf, "").split("\\.")(0) })
      .map({ entry =>
        val (key, uncookedVal) = entry
        val t =
          Try {
            val replacedPh = uncookedVal.map(kv => kv._1 -> replaceDifficultPlaceHolders(kv._2))
            init(s"$prf$key", AllApplicationProperties(replacedPh), configurationInitialise)
          }
        (key, t)
      }
      ).span { s => s._2.isFailure }

    if (stringToT._1.isEmpty)
      Right(stringToT._2.collect { case (key: String, value: Success[T]) => key -> value.value })
    else Left(ReadConfigErrors(stringToT._1.collect { case (key: String, value: Failure[T]) => s"error by prefix-> '$prf' with key-> '$key' error: ${value.exception.getMessage}" }.toList))
  }

  private def fullPrefix(prefix: String): String =
    if (prefix == null || prefix.endsWith("."))
      prefix
    else s"$prefix."

  private def filterByPrefix(prf: String, m: Map[String, String]): Map[String, String] = {
    if (prf == null) throw new IllegalStateException("Prefix must not null")
    else m.filter(entry => entry._1.startsWith(prf))
  }

  private def filterAndMap(prf: String, m: Map[String, String]): Map[String, String] = {
    filterByPrefix(prf, m)
      .map(entry => (entry._1.replace(prf, ""), entry._2))
  }

  private def getPropsFromMap(props: Map[String, String])(implicit configurationInitialise: ConfigurationInitialise[_]): Properties = {
    val replacedPh = props.map(kv =>kv._1 -> replaceDifficultPlaceHolders(kv._2))

    import scala.collection.JavaConverters.mapAsJavaMapConverter
    val properties = new Properties()
    properties.putAll(replacedPh.asJava)
    properties
  }

  @tailrec
  def extractNamesPlaceholder(
                               propertyValue: String,
                               inListPlace: List[String] = List()
                             ): List[String] = {
    val beginIndex = propertyValue.indexOf("${")
    val endIndex = propertyValue.indexOf("}")

    if (beginIndex != -1 && endIndex != -1 && beginIndex < endIndex) {
      val substring = propertyValue.substring(beginIndex + 2, endIndex)
      val propertyName = propertyValue.substring(endIndex + 1)
      val plus = inListPlace :+ substring
      extractNamesPlaceholder(propertyName, plus)
    } else inListPlace
  }
  @tailrec
  def replaceDifficultPlaceHolders(
                                    propertyValue: String,
                                    resultSrt: String =""
                                  )(implicit configurationInitialise: ConfigurationInitialise[_]): String = {

    val phListForResolve = extractNamesPlaceholder(propertyValue)

    if (phListForResolve.isEmpty)
      resultSrt+propertyValue
    else {
      val beginIndex = propertyValue.indexOf("${")
      val endIndex = propertyValue.indexOf("}")

      if (beginIndex != -1 && endIndex != -1 && beginIndex < endIndex) {
        val prf = propertyValue.substring(0, beginIndex)
        val newVal = configurationInitialise.resolvablePlaceHolders.getOrElse(phListForResolve.head, s"$${${phListForResolve.head}}")
        val end = propertyValue.substring(endIndex+1)

        val str1 = resultSrt+prf + newVal
        replaceDifficultPlaceHolders(end, str1)
      } else resultSrt+propertyValue


    }
  }

}
