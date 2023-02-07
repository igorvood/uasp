package ru.vtb.uasp.common.mask.dto

import play.api.libs.json.JsValue
import ru.vtb.uasp.common.mask.fun.{JsBooleanMaskedFun, JsNumberMaskedFun, JsStringMaskedFun, MaskedFun}


sealed trait JsMaskedPath {
  def addWithFun[IN, T <: JsValue](pathNodeList: List[String], maskedFun: MaskedFun[IN, T]): JsMaskedPath

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