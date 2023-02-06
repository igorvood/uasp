package ru.vtb.uasp.common.mask

import play.api.libs.json.{JsBoolean, JsNumber, JsString}

abstract class JsStringMaskedFun extends MaskedFun[JsString]

abstract class  JsNumberMaskedFun extends MaskedFun[JsNumber]

abstract class  JsBooleanMaskedFun extends MaskedFun[JsBoolean]
