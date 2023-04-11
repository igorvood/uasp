package ru.vtb.ie.generate.json.datamodel

import ru.vtb.ie.generate.json.abstraction.AbstractStringIdentyfyedEntity
import ru.vtb.ie.generate.json.dsl.Predef.PropAssoc

case class IntegrationElementJsonMeta(name: String) extends AbstractStringIdentyfyedEntity {
  override def entityName: String = name

  override def fields = Set(
    "customer_id" asStr { (id, _) => id }
  )
}
