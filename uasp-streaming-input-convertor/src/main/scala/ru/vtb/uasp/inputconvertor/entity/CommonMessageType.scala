package ru.vtb.uasp.inputconvertor.entity

import org.json4s._

import java.time.{LocalDateTime, ZoneId}

case class CommonMessageType(
                              message_key: String, // используется
                              json_message: JValue,
                            @deprecated
                              outBytes: Option[Array[Byte]] = None, // используется
                            )
