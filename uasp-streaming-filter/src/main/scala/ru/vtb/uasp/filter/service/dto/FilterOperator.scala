package ru.vtb.uasp.filter.service.dto

sealed trait FilterOperator[T <: Comparable[T]] {
  def compare(operand: Option[T], value: Option[T]): Boolean
}

case class NotEquals[T <: Comparable[T]]() extends FilterOperator[T] {
  override def compare(operand: Option[T], value: Option[T]): Boolean =
    operand.exists(op => value.exists(v => op.compareTo(v) != 0))
}

case class Equals[T <: Comparable[T]]() extends FilterOperator[T] {
  override def compare(operand: Option[T], value: Option[T]): Boolean =
    operand.exists(op => value.exists(v => op.compareTo(v) == 0))
}

case class Grater[T <: Comparable[T]]() extends FilterOperator[T] {
  override def compare(operand: Option[T], value: Option[T]): Boolean =
    operand.exists(op => value.exists(v => op.compareTo(v) > 0))
}

case class GraterOrEq[T <: Comparable[T]]() extends FilterOperator[T] {
  override def compare(operand: Option[T], value: Option[T]): Boolean =
    operand.exists(op => value.exists(v => op.compareTo(v) >= 0))
}

case class Less[T <: Comparable[T]]() extends FilterOperator[T] {
  override def compare(operand: Option[T], value: Option[T]): Boolean =
    operand.exists(op => value.exists(v => op.compareTo(v) < 0))
}

case class LessOrEq[T <: Comparable[T]]() extends FilterOperator[T] {
  override def compare(operand: Option[T], value: Option[T]): Boolean =
    operand.exists(op => value.exists(v => op.compareTo(v) <= 0))
}

case class Null[T <: Comparable[T]]() extends FilterOperator[T] {
  override def compare(operand: Option[T], value: Option[T]): Boolean = operand.isEmpty
}

case class NotNull[T <: Comparable[T]]() extends FilterOperator[T] {
  override def compare(operand: Option[T], value: Option[T]): Boolean = operand.nonEmpty
}