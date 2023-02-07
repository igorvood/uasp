package ru.vtb.uasp.common.mask

import play.api.libs.json.JsValue
import ru.vtb.uasp.common.mask.dto.JsMaskedPath.{JsMaskedPathObject, listToJsonPath}
import ru.vtb.uasp.common.mask.dto.{JsMaskedPath, JsMaskedPathError}

import scala.annotation.tailrec

object MaskedPredef {

  implicit class PathFactory(val self: Iterable[MaskedStrPathWithFunName]) extends AnyVal {

    def toJsonPath(): Either[List[JsMaskedPathError], JsMaskedPath] = {

      listToJsonPath(self, Right(JsMaskedPathObject(Map())))
    }

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
