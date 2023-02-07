package ru.vtb.uasp.common.mask.fun

import play.api.libs.json.{JsBoolean, JsNumber, JsString, JsValue}

trait MaskedFun[-IN, +T <: JsValue] {

  def mask(in: IN): T

  val name = this.getClass.getName

}

trait JsStringMaskedFun extends MaskedFun[String, JsString]

trait JsNumberMaskedFun extends MaskedFun[BigDecimal, JsNumber]

trait JsBooleanMaskedFun extends MaskedFun[Boolean, JsBoolean]

