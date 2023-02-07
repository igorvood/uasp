package ru.vtb.uasp.common.mask.dto

import play.api.libs.json.JsValue
import ru.vtb.uasp.common.mask.MaskedPredef.PathFactory
import ru.vtb.uasp.common.mask.MaskedStrPathWithFunName
import ru.vtb.uasp.common.mask.fun.{JsBooleanMaskedFun, JsNumberMaskedFun, JsStringMaskedFun, MaskedFun}
import ru.vtb.uasp.common.utils.config.PropertyUtil.asMap
import ru.vtb.uasp.common.utils.config.{AllApplicationProperties, ConfigurationInitialise, PropertyCombiner, ReadConfigErrors}

import scala.collection.immutable


sealed trait JsMaskedPath {
  def addWithFun[IN, T <: JsValue](pathNodeList: List[String], maskedFun: MaskedFun[IN, T]): JsMaskedPath

}

object JsMaskedPath extends PropertyCombiner[JsMaskedPath] {

  override protected def createMayBeErr[CONFIGURATION](prf: String)(implicit appProps: AllApplicationProperties, configurationInitialise: ConfigurationInitialise[CONFIGURATION]): Either[ReadConfigErrors, JsMaskedPath] = {
    val errorsOrProperties: Either[ReadConfigErrors, immutable.Iterable[MaskedStrPathWithFunName]] = asMap(prf).map(mapProp => mapProp.map(prop => MaskedStrPathWithFunName(prop._1, prop._2)))
    errorsOrProperties match {
      case Left(readConfigErrors) => Left(readConfigErrors)
      case Right(maskedStrPathWithFunNames) => maskedStrPathWithFunNames.toJsonPath() match {
        case Left(jsMaskedPathErrors) => Left(ReadConfigErrors(jsMaskedPathErrors.map(a => a.error)))
        case Right(jsMaskedPath) => Right(jsMaskedPath)
      }
    }
  }

}


case class JsMaskedPathObject(
                               inner: Map[String, JsMaskedPath] = Map()) extends JsMaskedPath {


  override def addWithFun[IN, T <: JsValue](pathNodeList: List[String], maskedFun: MaskedFun[IN, T]): JsMaskedPath = {
    val product = pathNodeList match {
      case Nil => this
      case head :: Nil =>
        inner.get(head)
          .map(_ => throw throw new IllegalArgumentException(s"Wrong structure '${head}' it is value, but '${inner.keys}' all ready registered like object")
          )
          .getOrElse {
            val value = maskedFun match {
              case fun: JsBooleanMaskedFun => JsBooleanMaskedPathValue(fun)
              case fun: JsNumberMaskedFun => JsNumberMaskedPathValue(fun)
              case fun: JsStringMaskedFun => JsStringMaskedPathValue(fun)
              case _ => ???
            }
            JsMaskedPathObject(inner = inner ++ Map(head -> value))
          }

      case head :: xs => {
        val maybePath1 = inner
          .get(head)
        val path = JsMaskedPathObject(inner = Map()).addWithFun(xs, maskedFun)
        val maybePath = maybePath1
          .map(j =>
            j match {
              case JsMaskedPathObject(_) => JsMaskedPathObject(inner ++ Map(head -> j.addWithFun(xs, maskedFun)))
              case _ => throw new IllegalArgumentException(s"Wrong structure '${(head :: xs).mkString(".")}' it is object, but '${head}' all ready registered like value")
            }
          )
          .getOrElse(
            JsMaskedPathObject(inner = inner ++ Map(head -> path))
          )
        maybePath
      }

    }
    product
  }
}


trait JsMaskedPathValueTrait extends JsMaskedPath {

  override def addWithFun[IN, T <: JsValue](pathNodeList: List[String], maskedFun: MaskedFun[IN, T]): JsMaskedPath =
    pathNodeList match {
      case Nil => this
      case x :: Nil => JsMaskedPathObject(inner = Map(x -> this))
      case x :: xs => throw new IllegalArgumentException("UNABLE TO ADD")
    }


}

case class JsStringMaskedPathValue(maskFun: JsStringMaskedFun) extends JsMaskedPathValueTrait

case class JsNumberMaskedPathValue(maskFun: JsNumberMaskedFun) extends JsMaskedPathValueTrait

case class JsBooleanMaskedPathValue(maskFun: JsBooleanMaskedFun) extends JsMaskedPathValueTrait