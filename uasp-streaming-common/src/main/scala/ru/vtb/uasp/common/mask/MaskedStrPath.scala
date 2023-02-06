package ru.vtb.uasp.common.mask

import play.api.libs.json.JsValue

import scala.util.Try

case class MaskedStrPath(strPath: String, maskedFunc: String){


  def maskedFunFactory[TT<:JsValue]() = {
    val value = Try{
      val value1 = Class.forName(maskedFunc)
      val value2 = value1.getDeclaredConstructor().newInstance()
      val value3 = value2.asInstanceOf[MaskedFun[TT]]
      value3
    }
    value
  }
}
