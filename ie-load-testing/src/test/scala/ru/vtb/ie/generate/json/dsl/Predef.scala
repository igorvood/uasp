package ru.vtb.ie.generate.json.dsl

import ru.vtb.ie.generate.json.service._

import scala.collection.immutable

object Predef {

  type NameField = String

  type GenerateFieldValueFunction[ID_TYPE, OUT_TYPE] = (ID_TYPE, NameField) => OUT_TYPE

  implicit final class PropAssoc(private val self: String) extends AnyVal {

    @inline def asConst[ID_TYPE](y: String): MetaProperty[ID_TYPE] =
      MetaProperty(self, (v1: ID_TYPE, v2: NameField) => StringType(y))

    @inline def asConst[ID_TYPE](y: BigDecimal): MetaProperty[ID_TYPE] =
      MetaProperty(self, (v1: ID_TYPE, v2: NameField) => NumberType(y))

    @inline def asConst[ID_TYPE](y: Boolean): MetaProperty[ID_TYPE] =
      MetaProperty(self, (v1: ID_TYPE, v2: NameField) => BooleanType(y))

    @inline def asObj[ID_TYPE](y: DataType[ID_TYPE]): MetaProperty[ID_TYPE] =
      MetaProperty(self, (v1: ID_TYPE, v2: NameField) => y)

    @inline def asStr[ID_TYPE](y: GenerateFieldValueFunction[ID_TYPE, String]):
    MetaProperty[ID_TYPE] =
      MetaProperty(self, { (i, w) => StringType(y(i, w)) })

    @inline def asNum[ID_TYPE](y: GenerateFieldValueFunction[ID_TYPE, BigDecimal]): MetaProperty[ID_TYPE] =
      MetaProperty(self, { (i, w) => NumberType(y(i, w)) })

    @inline def asBool[ID_TYPE](y: GenerateFieldValueFunction[ID_TYPE, Boolean]): MetaProperty[ID_TYPE] =
      MetaProperty(self, { (i, w) => BooleanType(y(i, w)) })

    @inline def asNull[ID_TYPE]: MetaProperty[ID_TYPE] =
      MetaProperty(self, { (i, w) => NullType() })


    @inline def asList[ID_TYPE](generateId: (ID_TYPE, NameField) => immutable.Seq[ID_TYPE],
                                y: (ID_TYPE, NameField) => DataType[ID_TYPE]): MetaProperty[ID_TYPE] =
      MetaProperty(self, { (v1: ID_TYPE, v2: NameField) => ListType(generateId, y) })

  }

}
