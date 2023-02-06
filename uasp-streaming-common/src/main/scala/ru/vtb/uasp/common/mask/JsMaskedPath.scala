package ru.vtb.uasp.common.mask

import play.api.libs.json.{JsBoolean, JsNumber, JsString, JsValue}

import scala.annotation.tailrec

sealed trait JsMaskedPath{
  def addWithFun(pathNodeList: List[String], maskedFun: MaskedFun[JsValue]):JsMaskedPath

  def add(pathNodeList: List[String]): JsMaskedPath

}

object JsMaskedPath{


  implicit class PathFactory(val self: Iterable[MaskedStrPathWithFunName]) extends AnyVal {

    def toJsonPath(): Either[List[JsMaskedPathError],JsMaskedPath] ={

      listToJsonPath(self, Right(JsMaskedPathObject( Map())))
    }

  }

  @tailrec
  private def listToJsonPath(l: Iterable[MaskedStrPathWithFunName], path: Either[List[JsMaskedPathError],JsMaskedPath]): Either[List[JsMaskedPathError],JsMaskedPath] = {
    l match {
      case Nil => path
      case x :: xs => {

        val res = for {
          maskedFun <- x.maskedFunFactory[JsValue]()
          p <- path
        } yield (p.addWithFun(x.strPath.split("\\.").toList, maskedFun))


        listToJsonPath(xs, res)

//        val either = x.maskedFunFactory[JsValue]()
//          .map(mf => )
//
//
//        val stringOrPath: Either[String, JsMaskedPath] = for {
//          maskedFun <- x.maskedFunFactory[JsValue]()
//          pa <- path.addWithFun(x.strPath.split("\\.").toList, maskedFun)
////          path1 = pa.addWithFun(x.strPath.split("\\.").toList, maskedFun)
//        } yield listToJsonPath(xs, Right(path1))
//        println(stringOrPath)
//
//        val path1 = path.add(x.strPath.split("\\.").toList)
//        listToJsonPath(xs, path1)
      }
    }
  }


}

case class JsMaskedPathObject(
                           inner: Map[String, JsMaskedPath] = Map())  extends JsMaskedPath{


  override def addWithFun(pathNodeList: List[String], maskedFun: MaskedFun[JsValue]): JsMaskedPath = {
    val product = pathNodeList match {
      case Nil => this
      case head :: Nil =>
        inner.get(head)
          .map(_ => throw throw new IllegalArgumentException(s"Wrong structure '${head}' it is value, but '${inner.keys}' all ready registered like object")
          )
          .getOrElse{
            val value = maskedFun match {
              case fun: JsBooleanMaskedFun => JsBooleanMaskedPathValue(fun)
              case fun: JsNumberMaskedFun => JsNumberMaskedPathValue(fun)
              case fun: JsStringMaskedFun => JsStringMaskedPathValue(fun)
              case _ => ???
            }
            JsMaskedPathObject(inner = inner ++ Map(head -> value))}

      case head :: xs => {
        val maybePath1 = inner
          .get(head)
        val path = JsMaskedPathObject(inner = Map()).add(xs)
        val maybePath = maybePath1
          .map(j =>
            j match {
              case JsMaskedPathObject(_) => JsMaskedPathObject( inner ++ Map(head -> j.add(xs)))
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

  def add(pathNodeList: List[String]): JsMaskedPath ={
    val product = pathNodeList match {
      case Nil => this
      case head :: Nil =>
        inner.get(head)
          .map(_ => throw throw new IllegalArgumentException(s"Wrong structure '${head}' it is value, but '${inner.keys}' all ready registered like object")
          )
          .getOrElse(JsMaskedPathObject(inner = inner ++ Map(head -> JsMaskedPathValue({ q => q}))))

      case head :: xs => {
        val maybePath1 = inner
          .get(head)
        val path = JsMaskedPathObject(inner = Map()).add(xs)
        val maybePath = maybePath1
          .map(j =>
            j match {
              case JsMaskedPathObject(_) => JsMaskedPathObject( inner ++ Map(head -> j.add(xs)))
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
case class JsMaskedPathValue(maskFun: JsValue => JsValue /*= { q => q}*/) extends JsMaskedPath {

  override def addWithFun(toList: List[String], maskedFun: MaskedFun[JsValue]): JsMaskedPath = ???

  override def add(pathNodeList: List[String]): JsMaskedPath =
    pathNodeList match {
      case Nil => this
      case x::Nil => JsMaskedPathObject( inner = Map(x -> JsMaskedPathValue(maskFun)))
      case x::xs =>  throw new IllegalArgumentException("UNABLE TO ADD")
    }

}

sealed trait JsMaskedPathValueTrait[T<: JsValue] extends JsMaskedPath{
  val maskFun : MaskedFun[T]


  override def addWithFun(pathNodeList: List[String], maskedFun: MaskedFun[JsValue]): JsMaskedPath =  pathNodeList match {
    case Nil => this
    case x::Nil => JsMaskedPathObject( inner = Map(x -> this /*JsMaskedPathStringValue(maskFun)*/))
    case x::xs =>  throw new IllegalArgumentException("UNABLE TO ADD")
  }

  override def add(pathNodeList: List[String]): JsMaskedPath =
    pathNodeList match {
      case Nil => this
      case x::Nil => JsMaskedPathObject( inner = Map(x -> this /*JsMaskedPathStringValue(maskFun)*/))
      case x::xs =>  throw new IllegalArgumentException("UNABLE TO ADD")
    }

}

case class JsStringMaskedPathValue(override val maskFun: JsStringMaskedFun ) extends JsMaskedPathValueTrait[JsString]

case class JsNumberMaskedPathValue(override val maskFun: JsNumberMaskedFun ) extends JsMaskedPathValueTrait[JsNumber]

case class JsBooleanMaskedPathValue(override val maskFun: JsBooleanMaskedFun ) extends JsMaskedPathValueTrait[JsBoolean]