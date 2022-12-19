package ru.vtb.uasp.filter.service.dto

import java.lang

sealed trait OperandClass

case class LongOperand(value: Option[lang.Long]) extends OperandClass

case class IntOperand(value: Option[Integer]) extends OperandClass

case class FloatOperand(value: Option[lang.Float]) extends OperandClass

case class DoubleOperand(value: Option[lang.Double]) extends OperandClass

case class BigDecimalOperand(value: Option[BigDecimal]) extends OperandClass

case class StringOperand(value: Option[String]) extends OperandClass

case class BooleanOperand(value: Option[lang.Boolean]) extends OperandClass
