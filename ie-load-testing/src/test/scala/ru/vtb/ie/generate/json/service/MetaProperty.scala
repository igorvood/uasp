package ru.vtb.ie.generate.json.service

import ru.vtb.ie.generate.json.dsl.Predef.{GenerateFieldValueFunction, NameField}

case class MetaProperty[ID_TYPE](
                                  nameField: NameField,
                                  function: GenerateFieldValueFunction[ID_TYPE, DataType[ID_TYPE]]
                                ) {

  def apply(id: ID_TYPE): String = "\"" + nameField + "\"" + s": ${function(id, nameField).jsonValue(id, nameField)}"

}
