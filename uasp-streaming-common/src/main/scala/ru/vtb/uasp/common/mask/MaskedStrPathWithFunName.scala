package ru.vtb.uasp.common.mask

import play.api.libs.json.JsValue
import ru.vtb.uasp.common.mask.dto.JsMaskedPathError
import ru.vtb.uasp.common.mask.fun.MaskedFun

import java.lang.reflect.Constructor
import scala.collection.immutable
import scala.util.{Failure, Success, Try}

case class MaskedStrPathWithFunName(strPath: String, maskedFunc: String) {

  def maskedFunFactory[Q, TT <: JsValue](): Either[List[JsMaskedPathError], MaskedFun[Q, TT]] = {
    val value = Try {

      maskedFunc match {
        case strFun if strFun.contains("(") & strFun.contains(")") => {
          val begBracket = strFun.indexOf("(")
          val funName = strFun.substring(0, begBracket)
          val params = strFun.substring(begBracket + 1, strFun.indexOf(")")).split(",") //.map(_.asInstanceOf[Any])
          val jClass = Class.forName(funName)
          val value1: immutable.Seq[Constructor[_]] = jClass.getConstructors.toList

          val value3: MaskedFun[Q, TT] = value1 match {
            case x :: Nil => x.newInstance(params: _*).asInstanceOf[MaskedFun[Q, TT]]
            case _ => throw new IllegalStateException(s"Class $funName must have only one constructor. Must accept parameters $params")
          }
          value3
        }
        case strFun => Class.forName(strFun).getDeclaredConstructor().newInstance().asInstanceOf[MaskedFun[Q, TT]]
      }


    }
    value match {
      case Failure(exception) => Left(List(JsMaskedPathError(s"unable to load class $maskedFunc for $strPath. Cause ${exception.getMessage}")))
      case Success(value) => Right(value)
    }

  }
}
