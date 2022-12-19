package ru.vtb.uasp.mutator.service.dto

sealed trait MapClass {

  def value: Any

  def isNotNull: Boolean = Option(value).isDefined
}

case class LongMap(data: Long) extends MapClass {
  override def value: Any = data
}

case class IntMap(data: Int) extends MapClass {
  override def value: Any = data
}

case class FloatMap(data: Float) extends MapClass {
  override def value: Any = data
}

case class DoubleMap(data: Double) extends MapClass {
  override def value: Any = data
}

case class BigDecimalMap(data: BigDecimal) extends MapClass {
  override def value: Any = data
}

case class StringMap(data: String) extends MapClass {
  override def value: Any = data
}

case class BooleanMap(data: Boolean) extends MapClass {
  override def value: Any = data
}