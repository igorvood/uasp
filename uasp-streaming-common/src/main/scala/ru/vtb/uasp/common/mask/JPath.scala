package ru.vtb.uasp.common.mask

import scala.annotation.tailrec

sealed trait JPath{
  def fullPath: String

  def addNew(pathNodeList: List[String]): JPath

}

object JPath{

  implicit class PathFactory(val self: List[MaskedStrPath]) extends AnyVal {

    def toJsonPath(): JPath ={
      listToJsonPath(self, JPathObject("root", Set()))
    }

  }

  @tailrec
  def listToJsonPath(l: List[MaskedStrPath], path: JPath): JPath = {
    l match {
      case Nil => path
      case x :: xs => {
        val path1 = path.addNew(x.strPath.split("\\.").toList)
        listToJsonPath(xs, path1)
      }
    }
  }


}

case class JPathObject(name: String,
                       inner: Set[JPath]) extends JPath{
  override def fullPath: String =  inner
    .map {
      case JPathObject(value, inner) => inner.map(q => value + "." + q.fullPath).mkString("")
      case JPathValue(value) => value
    } .mkString("\n")

  def addNew(pathNodeList: List[String]): JPath =
    pathNodeList match {
      case Nil => this
      case head :: xs => {
        val maybePath = inner
          .find {
            case JPathValue(name) => name == head
            case JPathObject(name, _) => name == head
          }
        maybePath match {
          case Some(v) => {
            val newJsPath = v.addNew(xs)
            val cuttenSetPaths = inner - v
            JPathObject(name, cuttenSetPaths + newJsPath)
          }
          case None => {
            val newJsPath = JPathValue(head)
              .addNew(xs)
            JPathObject(name, inner + newJsPath)
          }
        }
      }
    }


}

case class JPathValue(name: String) extends JPath {
  override def fullPath: String = name

  override def addNew(pathNodeList: List[String]): JPath =
    pathNodeList match {
      case Nil => this
      case x::xs => {
        val newJsPath = JPathValue(x).addNew(xs)
        JPathObject(name, Set(newJsPath))
      }
    }

}