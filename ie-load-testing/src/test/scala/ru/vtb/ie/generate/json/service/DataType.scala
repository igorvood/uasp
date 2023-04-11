package ru.vtb.ie.generate.json.service

import ru.vtb.ie.generate.json.dsl.Predef.NameField

import java.text.DecimalFormat
import scala.collection.immutable

trait DataType[ID_TYPE] {
  def jsonValue(id: ID_TYPE, nameField: NameField): String
}

case class NullType[ID_TYPE]() extends DataType[ID_TYPE] {
  override def jsonValue(id: ID_TYPE, nameField: NameField): String = "null"
}
case class BooleanType[ID_TYPE](private val v: Boolean) extends DataType[ID_TYPE] {
  override def jsonValue(id: ID_TYPE, nameField: NameField): String = if (v) "true" else "false"
}

case class NumberType[ID_TYPE](private val v: BigDecimal, private val printFormat: String = "########################") extends DataType[ID_TYPE] {

  private val format = new DecimalFormat(printFormat)

  override def jsonValue(id: ID_TYPE, nameField: NameField): String = format.format(v)
}

case class StringType[ID_TYPE](private val v: String) extends DataType[ID_TYPE] {
  override def jsonValue(id: ID_TYPE, nameField: NameField): String = "\"" + v + "\""
}

case class ObjectType[ID_TYPE](private val meta: JsonEntityMeta[ID_TYPE]) extends DataType[ID_TYPE] {
  override def jsonValue(id: ID_TYPE, nameField: NameField): String = meta.jsonValue(id, nameField)
}


case class ListType[ID_TYPE](
                              private val generateId: (ID_TYPE, NameField) => immutable.Seq[ID_TYPE],
                              private val genVal: (ID_TYPE, NameField) => DataType[ID_TYPE]) extends DataType[ID_TYPE] {
  override def jsonValue(id: ID_TYPE, nameField: NameField): String = "[" +
    generateId(id, nameField)
      .map(nextId => genVal(nextId, nameField).jsonValue(nextId, nameField))
      .mkString(",") + "]"

}

