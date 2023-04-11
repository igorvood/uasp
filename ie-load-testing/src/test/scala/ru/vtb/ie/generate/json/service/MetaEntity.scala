package ru.vtb.ie.generate.json.service

case class MetaEntity[ID_TYPE](name: String,
                               property: Set[MetaProperty[ID_TYPE]]
                              )
