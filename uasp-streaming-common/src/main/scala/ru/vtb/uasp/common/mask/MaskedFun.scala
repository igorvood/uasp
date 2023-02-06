package ru.vtb.uasp.common.mask

import play.api.libs.json.JsValue

trait MaskedFun[T<: JsValue] {

  def mask(v: T): T

}
