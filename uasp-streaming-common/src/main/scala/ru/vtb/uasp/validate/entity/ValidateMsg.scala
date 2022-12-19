package ru.vtb.uasp.validate.entity

case class ValidateMsg(msg: String, obj: Option[Any]) {
  def this(msg: String) = this(msg, None)

  def this(msg: String, obj: Any) = this(msg, Some(obj))
}

object ValidateMsg {
  def apply(msg: String, obj: Option[Any]) = new ValidateMsg(msg, obj)

  def apply(msg: String, obj: Any) = new ValidateMsg(msg, obj)

  def apply(msg: String) = new ValidateMsg(msg)
}
