package ru.vtb.uasp.common.extension

object CommonExtension {
  implicit class Also[T](val obj: T) {
    def also[O](func: T => O) : O = func(obj)
  }
}
