package ru.vtb.uasp.common.mask

import play.api.libs.json.JsValue

import scala.util.{Failure, Success, Try}

case class MaskedStrPathWithFunName(strPath: String, maskedFunc: String){


  def maskedFunFactory[TT<:JsValue]() = {
    val value = Try{
      val value1 = Class.forName(maskedFunc)
      val value2 = value1.getDeclaredConstructor().newInstance()
      val value3 = value2.asInstanceOf[MaskedFun[TT]]
      value3
    }
    value match {
      case Failure(exception) => Left(s"unable to load class $maskedFunc for $strPath. Cause ${exception.getMessage}" )
      case Success(value) => Right(value)
    }

  }
}
