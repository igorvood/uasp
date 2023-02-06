package ru.vtb.uasp.common.mask

import play.api.libs.json.JsValue

trait MaskedFun[T<: JsValue] extends ((T) => T) {



}
