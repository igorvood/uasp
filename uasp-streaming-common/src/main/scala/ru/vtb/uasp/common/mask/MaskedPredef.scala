package ru.vtb.uasp.common.mask

import play.api.libs.json._
import ru.vtb.uasp.common.mask.dto._

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

object MaskedPredef {

  implicit class PathFactory(val self: Iterable[MaskedStrPathWithFunName]) extends AnyVal {

    /** преобразует коллекцию MaskedStrPathWithFunName в JsMaskedPath
     * @return
     */
    def toJsonPath(): Either[List[JsMaskedPathError], JsMaskedPath] = {

      listToJsonPath(self, Right(JsMaskedPathObject(Map())))
    }

  }


  implicit class MaskJsValuePredef(val self: JsValue) extends AnyVal {

    /** маскирует поля у входящего JsValue
     * @param maskedRule правила маскирования
     * @return JsValue с маскированными полями
     */
    def toMaskedJson(maskedRule: JsMaskedPath): Either[List[JsMaskedPathError], JsValue] = {
      val res = Try {
        maskData(self, maskedRule)
      } match {
        case Failure(exception) => Left(List(JsMaskedPathError(exception.getMessage)))
        case Success(value) => Right(value)
      }
      res
    }
  }

  private def maskData(jsObject: JsValue, path: JsMaskedPath): JsValue = {
    val value1: JsValue = (jsObject, path) match {
      case (JsObject(values), JsMaskedPathObject(masked)) => {
        val newVals = values
          .map(vv => {
            val tuple: (String, JsValue) = masked
              .get(vv._1)
              .map(q => vv._1 -> maskData(vv._2, q))
              .getOrElse(vv)
            tuple
          }
          )
        JsObject(newVals)
      }
      case (JsString(value), JsStringMaskedPathValue(masked)) => masked.mask(value)
      case (JsNumber(value), JsNumberMaskedPathValue(masked)) => masked.mask(value)
      case (JsBoolean(value), JsBooleanMaskedPathValue(masked)) => masked.mask(value)
      case (q, w) => throw new IllegalArgumentException(s"Unable to masked value '$q' wrapper class  ${q.getClass} with function -> ${w.getClass}")
    }
    value1
  }

  @tailrec
  private def listToJsonPath[IN, T <: JsValue](l: Iterable[MaskedStrPathWithFunName], path: Either[List[JsMaskedPathError], JsMaskedPath]): Either[List[JsMaskedPathError], JsMaskedPath] = {
    l match {
      case Nil => path
      case x :: xs => {
        val either = x.maskedFunFactory[IN, T]()
        val res =
          for {
            maskedFun <- either
            p <- path
          } yield (p.addWithFun[IN, T](x.strPath.split("\\.").toList, maskedFun))
        listToJsonPath(xs, res)
      }
    }

  }
}
