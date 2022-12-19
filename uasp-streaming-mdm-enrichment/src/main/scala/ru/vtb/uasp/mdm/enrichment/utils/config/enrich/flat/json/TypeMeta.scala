package ru.vtb.uasp.mdm.enrichment.utils.config.enrich.flat.json

sealed trait TypeMeta

case class StringMeta() extends TypeMeta

case class NumberMeta() extends TypeMeta

case class BooleanMeta() extends TypeMeta


object TypeMeta {

  def apply(typeStr: String): TypeMeta = {
    typeStr.toUpperCase() match {
      case "BOOLEAN" => BooleanMeta()
      case "STRING" => StringMeta()
      case "BIGDECIMAL" => NumberMeta()
      case "LONG" => NumberMeta()
      case "INT" => NumberMeta()
      case "DOUBLE" => NumberMeta()
      case "FLOAT" => NumberMeta()
      case v => throw new IllegalArgumentException(s"non supported argument value $v")
    }
  }
}