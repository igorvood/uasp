package ru.vtb.ie.generate.json.service

import ru.vtb.ie.generate.json.dsl.Predef.{GenerateFieldValueFunction, NameField}

import java.nio.charset.StandardCharsets
import scala.collection.immutable
import scala.math.abs

trait JsonEntityMeta[ID_TYPE] extends DataType[ID_TYPE] {

  type ID = ID_TYPE

  def convertHashToID(i: Int): ID_TYPE

  val defaultStr: GenerateFieldValueFunction[ID_TYPE, String] = { (id, nameField) => (id.hashCode + nameField.hashCode).toString }
  val defaultNum: GenerateFieldValueFunction[ID_TYPE, BigDecimal] = { (id, nameField) => id.hashCode + nameField.hashCode }
  val defaultBool: GenerateFieldValueFunction[ID_TYPE, Boolean] = { (id, nameField) =>
    if ((id.hashCode + nameField.hashCode) % 2 == 0) false else true
  }
  val defaultListNum: (ID_TYPE, NameField) => NumberType[ID_TYPE] = { (id, nameField) => NumberType(defaultNum(id, nameField)) }
  val defaultListStr: (ID_TYPE, NameField) => StringType[ID_TYPE] = { (id, nameField) => StringType(defaultStr(id, nameField)) }
  val defaultListBool: (ID_TYPE, NameField) => BooleanType[ID_TYPE] = { (id, nameField) => BooleanType(defaultBool(id, nameField)) }

  def genListCountDefault(min: Int = 1, max: Int = 3): (ID_TYPE, NameField) => immutable.Seq[ID_TYPE] = {
    require(min <= max)
    require(min >= 0)
    require(max >= 0)
    val result: (ID_TYPE, NameField) => immutable.Seq[ID_TYPE] = (id, name) => {
      val hash = abs(id.hashCode() + name.hashCode)
      val inclusive = 0 to (hash % (max - min) + min)
      inclusive.map(convertHashToID)
    }
    result
  }


  def entityName: String

  def jsonValue(id: ID_TYPE): String = jsonValue(id, entityName)

  def jsonValueAsBytes(id: ID_TYPE): Array[Byte] = jsonValue(id, entityName).getBytes(StandardCharsets.UTF_8)

  def fields: Set[MetaProperty[ID_TYPE]]

  protected def meta: MetaEntity[ID_TYPE] = MetaEntity(entityName, fields)

  def validateMeta(): Unit = {
    if (badFields.nonEmpty)
      throw new IllegalStateException(s"meta is not valid fields is dublicate $badFields")
  }

  private def badFields = fields
    .groupBy(k => k.nameField)
    .map(q => (q._1, q._2.size))
    .filter(q => q._2 > 1)

  protected def strProp(nameField: NameField)(dataGen: GenerateFieldValueFunction[ID_TYPE, String]): MetaProperty[ID_TYPE] =
    MetaProperty(nameField, (v1: ID_TYPE, v2: NameField) => StringType(dataGen(v1, v2)))

  protected def numProp(nameField: NameField)(dataGen: GenerateFieldValueFunction[ID_TYPE, BigDecimal]): MetaProperty[ID_TYPE] =
    MetaProperty(nameField, (v1: ID_TYPE, v2: NameField) => NumberType(dataGen(v1, v2)))

  protected def boolProp(nameField: NameField)(dataGen: GenerateFieldValueFunction[ID_TYPE, Boolean]): MetaProperty[ID_TYPE] =
    MetaProperty(nameField, (v1: ID_TYPE, v2: NameField) => BooleanType(dataGen(v1, v2)))

  protected def objProp(nameField: NameField)(metaEntity: JsonEntityMeta[ID_TYPE]): MetaProperty[ID_TYPE] =
    MetaProperty(nameField, (v1: ID_TYPE, v2: NameField) => ObjectType(metaEntity))

  protected def strConst(data: String): (ID_TYPE, NameField) => String = { (_, _) => data }

  protected def numConst(data: BigDecimal): (ID_TYPE, NameField) => BigDecimal = { (_, _) => data }

  protected def boolConst(data: Boolean): (ID_TYPE, NameField) => Boolean = { (_, _) => data }

  protected def strConstProp(nameField: NameField, data: String) =
    MetaProperty[ID_TYPE](nameField, {
      (_, _) => StringType(data)
    })

  protected def numConstProp(nameField: NameField, data: BigDecimal) =
    MetaProperty[ID_TYPE](nameField, {
      (_, _) => NumberType(data)
    })

  protected def boolConstProp(nameField: NameField, data: Boolean) =
    MetaProperty[ID_TYPE](nameField, {
      (_, _) => BooleanType(data)
    })

}
