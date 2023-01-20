package ru.vtb.uasp.common.generate.dto

case class PlaceholderDto(replaceValue: String,
                          calcNewValue: Option[String] = None,
                          upToHead: Boolean = false,

                         )
