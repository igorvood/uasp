package ru.vtb.uasp.common.mask

import play.api.libs.json.{JsBoolean, JsNumber, JsString, JsValue}

trait MaskedFun[T<: JsValue] extends (T => T)

trait JsStringMaskedFun extends MaskedFun[JsString]

trait JsNumberMaskedFun extends MaskedFun[JsNumber]

trait  JsBooleanMaskedFun extends MaskedFun[JsBoolean]

