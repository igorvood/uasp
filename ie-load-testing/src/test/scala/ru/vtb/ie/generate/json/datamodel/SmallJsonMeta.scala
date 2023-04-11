package ru.vtb.ie.generate.json.datamodel

import ru.vtb.ie.generate.json.abstraction.AbstractStringIdentyfyedEntity
import ru.vtb.ie.generate.json.dsl.Predef.PropAssoc

import scala.math.abs

case class SmallJsonMeta(name: String) extends AbstractStringIdentyfyedEntity {
  override def entityName: String = name

  override def fields = Set(
    "customer_id" asStr { (id, _) => id },
    "uuid" asStr { (_, _) => java.util.UUID.randomUUID().toString },
    "operation_id" asStr { (id, _) => (abs(id.hashCode) % 1000000).toString },
    "type" asConst "way4-case-2-2",
    "effective_date" asConst 1644943372d,
    "duration" asConst 0,
    "process_timestamp" asConst 1643184276285d,
  )
}
