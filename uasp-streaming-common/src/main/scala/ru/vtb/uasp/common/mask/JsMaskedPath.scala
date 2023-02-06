package ru.vtb.uasp.common.mask

import play.api.libs.json.{JsBoolean, JsNumber, JsString, JsValue}

import scala.annotation.tailrec

sealed trait JsMaskedPath{
  def addNew(pathNodeList: List[String]): JsMaskedPath

}

object JsMaskedPath{


  implicit class PathFactory(val self: Iterable[MaskedStrPath]) extends AnyVal {

    def toJsonPath(): JsMaskedPath ={

      listToJsonPath(self, JsMaskedPathObject( Map()))
    }

  }

  @tailrec
  private def listToJsonPath(l: Iterable[MaskedStrPath], path: JsMaskedPath): JsMaskedPath = {
    l match {
      case Nil => path
      case x :: xs => {
        val path1 = path.addNew(x.strPath.split("\\.").toList)
        listToJsonPath(xs, path1)
      }
    }
  }


}

case class JsMaskedPathObject(
                           inner: Map[String, JsMaskedPath] = Map())  extends JsMaskedPath{


  def addNew(pathNodeList: List[String]): JsMaskedPath ={
    val product = pathNodeList match {
      case Nil => this
      case head :: Nil =>
        inner.get(head)
          .map(_ => throw throw new IllegalArgumentException(s"Wrong structure '${head}' it is value, but '${inner.keys}' all ready registered like object")
          )
          .getOrElse(JsMaskedPathObject(inner = inner ++ Map(head -> JsMaskedPathValue())))

      case head :: xs => {
        val maybePath1 = inner
          .get(head)
        val path = JsMaskedPathObject(inner = Map()).addNew(xs)
        val maybePath = maybePath1
          .map(j =>
            j match {
              case JsMaskedPathObject(_) => JsMaskedPathObject( inner ++ Map(head -> j.addNew(xs)))
              case JsMaskedPathValue(_) => throw new IllegalArgumentException(s"Wrong structure '${(head :: xs).mkString(".")}' it is object, but '${head}' all ready registered like value")
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

@deprecated
case class JsMaskedPathValue(maskFun: JsValue => JsValue = { q => q}) extends JsMaskedPath {

  override def addNew(pathNodeList: List[String]): JsMaskedPath =
    pathNodeList match {
      case Nil => this
      case x::Nil => JsMaskedPathObject( inner = Map(x -> JsMaskedPathValue(maskFun)))
      case x::xs =>  throw new IllegalArgumentException("UNABLE TO ADD")
    }

}

sealed trait JsMaskedPathValueTrait[T<: JsValue] extends JsMaskedPath{
  val maskFun : MaskedFun[T]

  override def addNew(pathNodeList: List[String]): JsMaskedPath =
    pathNodeList match {
      case Nil => this
      case x::Nil => JsMaskedPathObject( inner = Map(x -> this /*JsMaskedPathStringValue(maskFun)*/))
      case x::xs =>  throw new IllegalArgumentException("UNABLE TO ADD")
    }

}

case class JsStringMaskedPathValue(override val maskFun: JsStringMaskedFun ) extends JsMaskedPathValueTrait[JsString]

case class JsNumberMaskedPathValue(override val maskFun: JsNumberMaskedFun ) extends JsMaskedPathValueTrait[JsNumber]

case class JsBooleanMaskedPathValue(override val maskFun: JsBooleanMaskedFun ) extends JsMaskedPathValueTrait[JsBoolean]