package ru.vtb.uasp.common.mask

import play.api.libs.json.{JsBoolean, JsNumber, JsString}

trait JsStringMaskedFun extends MaskedFun[JsString]

trait JsNumberMaskedFun extends MaskedFun[JsNumber]

trait  JsBooleanMaskedFun extends MaskedFun[JsBoolean]
