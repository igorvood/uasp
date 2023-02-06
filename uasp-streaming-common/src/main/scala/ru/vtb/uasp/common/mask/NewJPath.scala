package ru.vtb.uasp.common.mask

import play.api.libs.json.JsValue

import scala.annotation.tailrec

sealed trait NewJPath{
  def addNew(pathNodeList: List[String]): NewJPath

}

object NewJPath{


  implicit class PathFactory(val self: Iterable[MaskedStrPath]) extends AnyVal {

    def toJsonPath18(): NewJPath ={

      listToJsonPath18(self, NewJPathObject( Map()))
    }

  }

  @tailrec
  def listToJsonPath18(l: Iterable[MaskedStrPath], path: NewJPath): NewJPath = {
    l match {
      case Nil => path
      case x :: xs => {
        val path1 = path.addNew(x.strPath.split("\\.").toList)
        listToJsonPath18(xs, path1)
      }
    }
  }


}

case class NewJPathObject(
                           inner: Map[String, NewJPath] = Map())  extends NewJPath{


  def addNew(pathNodeList: List[String]): NewJPath ={
    val product = pathNodeList match {
      case Nil => this
      case head :: Nil =>
        inner.get(head)
          .map(_ => throw throw new IllegalArgumentException(s"Wrong structure '${head}' it is value, but '${inner.keys}' all ready registered like object")
          )
          .getOrElse(NewJPathObject(inner = inner ++ Map(head -> NewJPathValue())))

      case head :: xs => {
        val maybePath1 = inner
          .get(head)
        val path = NewJPathObject(inner = Map()).addNew(xs)
        val maybePath = maybePath1
          .map(j =>
            j match {
              case NewJPathObject(_) => NewJPathObject( inner ++ Map(head -> j.addNew(xs)))
              case NewJPathValue(_) => throw new IllegalArgumentException(s"Wrong structure '${(head :: xs).mkString(".")}' it is object, but '${head}' all ready registered like value")
            }
          )
          .getOrElse(
            NewJPathObject(inner = inner ++ Map(head -> path))
          )
        maybePath
      }

    }
    product
  }

}

case class NewJPathValue(maskFun: JsValue => JsValue = { q => q}) extends NewJPath {

  override def addNew(pathNodeList: List[String]): NewJPath =
    pathNodeList match {
      case Nil => this
      case x::Nil => NewJPathObject( inner = Map(x -> NewJPathValue(maskFun)))
      case x::xs =>  throw new IllegalArgumentException("UNABLE TO ADD")
    }

}