package ru.vtb.uasp.common.generate.dto

case class ReplaceDto(value: String,
                      placeholder: Option[PlaceholderDto] = None)
